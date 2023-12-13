package com.offer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
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
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper shortLinkGotoMapper;

    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {

        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        LambdaUpdateWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaUpdate(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
        ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
        if (shortLinkGotoDO == null) {
            // TODO 严禁来说，此处需要风控
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
            ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());

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


        return page.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
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
