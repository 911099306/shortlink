package com.offer.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.common.convention.result.Results;
import com.offer.shortlink.admin.remote.ShortLinkRemoteService;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.*;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/12/5
 **/
@RestController
@RequestMapping("/api/short-link/admin/v1")
public class ShortLinkController {

    /**
     * 后期优化为Spring cloud  使用 openFeign调用
     */
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};


    /**
     * 短链接分页查询
     * @param requestParam 短链接分页查询请求参数
     * @return 短链接分页查询相应参数
     *
     */
    @GetMapping("/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }

    /**
     * 远程调用 project 进行短链接创建请求
     * @param requestParam 短链接创建参数
     * @return 短链接创建响应
     */
    @PostMapping("/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 远程调用 project 进行短链接修改请求
     *
     *     "validDateType": 73,
     *     "validDate": "27",
     *     "describe": "ut elit"
     */
    @PostMapping("/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }
}
