package com.offer.shortlink.admin.controller;

import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.common.convention.result.Results;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.offer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.offer.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/api/short-link/v1/group")
    public Result<Void> saveGroup( @RequestBody ShortLinkGroupSaveReqDTO requestParam) {
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }

    @GetMapping("/api/short-link/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> selectGroupList() {
        return Results.success(groupService.selectGroupList());
    }


}
