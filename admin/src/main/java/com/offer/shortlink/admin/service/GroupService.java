package com.offer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.offer.shortlink.admin.dao.entity.GroupDO;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.offer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

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

    /**
     * 查询用户短链接分组结合
     * @return 短链接用户分组集合
     */
    List<ShortLinkGroupRespDTO> selectGroupList();

    /**
     * 根据username修改分组名称
     * @param requestParam 修改分组参数
     */
    void updateGroupName(ShortLinkGroupUpdateReqDTO requestParam);

    /**
     * 删除短链接分组
     * @param gid 短链接分组标示
     */
    void deleteGroup(String gid);

    /**
     *  短链接分组排序
     * @param requestParam 短链接分组排序功能参数
     */
    void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam);
}
