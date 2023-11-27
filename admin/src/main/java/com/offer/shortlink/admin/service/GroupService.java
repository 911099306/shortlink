package com.offer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.offer.shortlink.admin.dao.entity.GroupDO;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
public interface GroupService extends IService<GroupDO> {


    /**
     * 新增短链接分组
     * @param groupName 分组名称
     */
    void saveGroup(String groupName);

}
