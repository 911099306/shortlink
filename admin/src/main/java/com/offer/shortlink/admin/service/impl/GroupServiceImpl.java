package com.offer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.admin.biz.user.UserContext;
import com.offer.shortlink.admin.common.convention.exception.ClientException;
import com.offer.shortlink.admin.common.convention.exception.ServiceException;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.dao.entity.GroupDO;
import com.offer.shortlink.admin.dao.mapper.GroupMapper;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.offer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.offer.shortlink.admin.remote.ShortLinkRemoteService;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.offer.shortlink.admin.service.GroupService;
import com.offer.shortlink.admin.toolkit.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO>
        implements GroupService {

     ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};

    /**
     * 新增短链接分组
     * @param groupName 分组名称
     */
    @Override
    public void saveGroup(String groupName) {
        GroupDO groupDO = GroupDO.builder()
                .gid(generateGid())
                .username(UserContext.getUsername())
                .name(groupName)
                .sortOrder(0)
                .build();

        this.save(groupDO);

    }

    @Override
    public List<ShortLinkGroupRespDTO> selectGroupList() {
        // TODO 返回用户名
        List<GroupDO> groupDOList = this.lambdaQuery()
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .orderByDesc(GroupDO::getSortOrder)
                .orderByDesc(GroupDO::getCreateTime)
                .list();
        List<String> gids = groupDOList.stream().map(item -> item.getGid()).collect(Collectors.toList());
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkRemoteService.countPerGroup(gids);
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOS = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
        shortLinkGroupRespDTOS.forEach(shortLinkGroup->{
            Optional<ShortLinkGroupCountQueryRespDTO> first = listResult.getData()
                    .stream().filter(item -> item.getGid().equals(shortLinkGroup.getGid())).findFirst();
            first.ifPresent(item -> shortLinkGroup.setShortLinkCount(first.get().getShortLinkCount()));
        });



        return shortLinkGroupRespDTOS;
    }

    /**
     * 修改短链接分组
     * @param requestParam 修改分组请求参数
     */
    @Override
    public void updateGroupName(ShortLinkGroupUpdateReqDTO requestParam) {
        // 获取当前登录人
        String username = UserContext.getUsername();

        // 根据 gid 查询分组，并判断当前分组是否属于登录用户,且 未删除，
        GroupDO groupDO = this.lambdaQuery()
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0)
                .one();
        if (groupDO == null) {
            throw new ClientException("该分组不存在");
        }

        // 再进行修改
        boolean update = this.lambdaUpdate()
                .eq(GroupDO::getGid,requestParam.getGid())
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag,0)
                .set(GroupDO::getName, requestParam.getName())
                .update();

        // 修改失败则抛出异常
        if (!update) {
            throw new ClientException("修改分组名称失败");
        }
    }

    @Override
    public void deleteGroup(String gid) {
        // 获取当前登录用户
        String username = UserContext.getUsername();
        GroupDO groupDO = this.lambdaQuery()
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0)
                .one();
        if (groupDO == null) {
            throw new ClientException("短链接分组不存在");
        }
        // 删除
        boolean update = this.lambdaUpdate()
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0)
                .set(GroupDO::getDelFlag, 1)
                .update();

        if (!update) {
            throw new ClientException("删除短链接分组失败");
        }
    }

    /**
     * 短链接分组排序
     * @param requestParam 短链接分组排序功能参数
     */
    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        // 获取用户名
        String username = UserContext.getUsername();
        requestParam.forEach(item->{
            GroupDO groupDO = this.lambdaQuery()
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getGid, item.getGid())
                    .eq(GroupDO::getDelFlag, 0)
                    .one();
            if (groupDO == null) {
                throw new ClientException("分组记录不存在");
            }

            boolean update = this.lambdaUpdate()
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getGid, item.getGid())
                    .eq(GroupDO::getDelFlag, 0)
                    .set(GroupDO::getSortOrder, item.getSortOrder())
                    .update();
            if (!update) {
                throw new ServiceException("排序字段更新失败");
            }
        });
    }

    private String generateGid() {
        // TODO 优化redis。 作为分组，应该是经常查询且不变，存入redis
        boolean hasGid = false;
        String gid = null;
        while (!hasGid) {
            gid = RandomGenerator.generateRandomString();
            GroupDO hasGroupFlag = this.lambdaQuery()
                    .eq(GroupDO::getGid, gid)
                    .eq(GroupDO::getUsername,UserContext.getUsername())
                    .one();
            hasGid = hasGroupFlag == null;
        }
        return gid;
    }
}
