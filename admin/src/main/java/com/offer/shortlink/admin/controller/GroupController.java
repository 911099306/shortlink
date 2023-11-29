package com.offer.shortlink.admin.controller;

import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.common.convention.result.Results;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.offer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.offer.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增短链接分组
     * @param requestParam 短链接分组名称
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> saveGroup(@RequestBody ShortLinkGroupSaveReqDTO requestParam) {
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }

    /**
     * 查询当前用户短链接分组集合
     * @return 短链接分组集合
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> selectGroupList() {
        return Results.success(groupService.selectGroupList());
    }

    /**
     * 修改短链接分组名称
     * @param requestParam  修改短链接分组名称请求参数
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroupName(@RequestBody ShortLinkGroupUpdateReqDTO requestParam) {
        groupService.updateGroupName(requestParam);
        return Results.success();
    }

    /**
     * 删除短链接分组
     * @param gid  短链接分组标识
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroupName(@RequestParam("gid") String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }

    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void> sortGroup(@RequestBody List<ShortLinkGroupSortReqDTO> requestParam) {

        groupService.sortGroup(requestParam);
        return Results.success();
    }



}
