package com.offer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.project.common.convention.exception.ClientException;
import com.offer.shortlink.project.common.convention.exception.ServiceException;
import com.offer.shortlink.project.common.enums.ValiDateTypeEnum;
import com.offer.shortlink.project.dao.entity.ShortLinkDO;
import com.offer.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.offer.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.offer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.offer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.offer.shortlink.project.service.ShortLinkService;
import com.offer.shortlink.project.toolkit.HashUtil;
import com.offer.shortlink.project.toolkit.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.offer.shortlink.project.common.constant.RedisKeyConstant.*;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/29
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {


    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {

        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        // 查询redis， 看是否已经查询过该信息
        String originalLink = redisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));

        if (StrUtil.isAllNotBlank(originalLink)) {
            ((HttpServletResponse) response).sendRedirect(originalLink);
            return;
        }

        // 预防缓存传统
        boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            // 不存在，数据库中一定没有，直接返回
            // 不需要缓存空对象，减少内存占用
            return;
        }

        String gotoIsNulShortLink = redisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(gotoIsNulShortLink)) {
            // 双重锁检查，尽量减少对数据库的访问
            return;
        }


        // 防止缓存击穿 redKey
        // 使用分布式锁 redisson， 进行加锁，使得只有一个请求会打到mysql内
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            // 双检加锁机制
            originalLink = redisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originalLink)){
                // 非空，已经有一个线程进行 回写 了，直接进行跳转
                ((HttpServletResponse) response).sendRedirect(originalLink);
                return;
            }

            // 仍未空，所以让其进行查询，在跳转前，回写到redis
            LambdaUpdateWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaUpdate(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
            if (shortLinkGotoDO == null) {
                // TODO 严禁来说，此处需要风控
                // 缓存一个空对象, 随笔设置一个控制即可
                redisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-",30, TimeUnit.SECONDS);
                return;
            }

            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid,  shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            if (shortLinkDO != null) {
                // 开始跳转了。
                redisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),shortLinkDO.getOriginUrl(),3,TimeUnit.MINUTES);
                ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
            }
        } finally {
            lock.unlock();
        }



    }

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建信息
     */
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {

        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = StrBuilder.create(requestParam.getDomain())
                .append("/").append(shortLinkSuffix).toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .shortUri(shortLinkSuffix)
                .enableStatus(0)
                .fullShortUrl(fullShortUrl)
                .build();
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(shortLinkDO.getGid())
                .build();
        try {
            this.save(shortLinkDO);

            shortLinkGotoMapper.insert(shortLinkGotoDO);

        } catch (DuplicateKeyException ex) {
            // TODO 已经误判的短链接如何处理
            // 第一种，短链接确实真实存在缓存
            // 第二种，短链接不一定存在缓存中？？
            ShortLinkDO hasShortLinkDO = this.lambdaQuery()
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .one();
            if (hasShortLinkDO != null) {
                log.warn("短链接：{} 重复入库", fullShortUrl);
                throw new ServiceException("短链接生成重复");
            }
        }
        // 加入缓存  进行预热
        redisTemplate.opsForValue().set(
                String.format(GOTO_SHORT_LINK_KEY,fullShortUrl),
                requestParam.getOriginUrl(),
                LinkUtil.getLinkCacheValidTime(requestParam.getValidDate()),
                TimeUnit.MILLISECONDS);
        // 加入布隆过滤器，
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl( "http://" + fullShortUrl)
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .build();
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {

        // TODO 应该先判断一下这个是不是登录用户自己的gid
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);

        if (hasShortLinkDO == null) {
            throw new ClientException("短连接信息不存在");
        }

        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .shortUri(hasShortLinkDO.getShortUri())
                .clickNum(hasShortLinkDO.getClickNum())
                // 应该是 也可以修改的吖
                // .favicon(requestParam.getFavicon())
                .favicon(hasShortLinkDO.getFavicon())
                .createdType(hasShortLinkDO.getCreatedType())
                // 为什么这个不修改 ?
                // .fullShortUrl(requestParam.getFullShortUrl())
                .domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .build();

        if (Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
            // 当 不修改 gid， 直接在原位置进行修改
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    // 若 有效期类型为 永久， 则设置有效期时间为null
                    .set(Objects.equals(requestParam.getValidDateType(), ValiDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);
            baseMapper.update(shortLinkDO, updateWrapper);
        }else{
            /// 修改 gid ，需要先删除之前的link， 然后新建
            // 这里好像有问题，没有穿如新的gid，是不可能走到这里的。
            LambdaQueryWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            baseMapper.insert(shortLinkDO);
        }
    }

    /**
     * 短链接分页查询
     *
     * @param requestParam 短链接分页查询参数
     */
    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {

        IPage<ShortLinkDO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());

        this.lambdaQuery()
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .orderByDesc(ShortLinkDO::getCreateTime)
                .page(page);


        return page.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> countPerGroup(List<String> requestParam) {
        //  因为 ShortDO 内部没有 count（*），所以使用 click_num 先进行占位
        List<ShortLinkDO> list = this.query()
                .select("gid ,count(*) as click_num")
                .eq("enable_status", 0)
                .eq("del_flag", 0)
                // 还有一个有效期，是否需要纳入统计？？？
                .in("gid", requestParam)
                .groupBy("gid")
                .list();
        return list.stream()
                .map(item ->
                    ShortLinkGroupCountQueryRespDTO.builder()
                        .gid(item.getGid())
                        .shortLinkCount(item.getClickNum())
                        .build()
                ).collect(Collectors.toList());
    }

    /**
     * 根据 字符串 生成短链接标识
     *
     * @param requestParam
     * @return
     */
    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();

        int customGenerateCount = 0;
        String shortUri = null;
        while (true) {
            if (customGenerateCount >= 10) {
                throw new ServiceException("短链接频繁生成，请稍后再试~");
            }
            shortUri = HashUtil.hashToBase62(originUrl);

            /* 版本 v1.0 查询数据库，保证唯一性
           ShortLinkDO hasFullShortUri = this.lambdaQuery()
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getDomain() + "/" + originUrl).one();
            if (hasFullShortUri == null) {
                break;
            }*/
            boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + originUrl);
            if (!contains) {
                break;
            }
            customGenerateCount++;
        }
        return shortUri;
    }
}
