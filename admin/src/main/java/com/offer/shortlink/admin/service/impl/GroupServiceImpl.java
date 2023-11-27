package com.offer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.admin.dao.entity.GroupDO;
import com.offer.shortlink.admin.dao.mapper.GroupMapper;
import com.offer.shortlink.admin.service.GroupService;
import com.offer.shortlink.admin.toolkit.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO>
        implements GroupService {


    /**
     * 新增短链接分组
     * @param groupName 分组名称
     */
    @Override
    public void saveGroup(String groupName) {
        GroupDO groupDO = GroupDO.builder()
                .gid(generateGid())
                // TODO 传入username
                // .username()
                .name(groupName)
                .build();

        this.save(groupDO);

    }

    private String generateGid() {
        // TODO 优化redis。 作为分组，应该是经常查询且不变，存入redis
        boolean hasGid = false;
        String gid = null;
        while (!hasGid) {
            gid = RandomGenerator.generateRandomString();
            GroupDO hasGroupFlag = this.lambdaQuery()
                    .eq(GroupDO::getGid, gid)
                    // TODO 设置用户名
                    .eq(GroupDO::getUsername, null)
                    .one();
            hasGid = hasGroupFlag == null;
        }
        return gid;
    }
}
