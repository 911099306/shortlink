package com.offer.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.offer.shortlink.project.common.convention.result.Result;
import com.offer.shortlink.project.common.convention.result.Results;
import com.offer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.offer.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/29
 * <p>
 * 短链接控制层
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/v1")
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }


    /**
     * 修改短链接信息
     * @return
     */
    @PostMapping("/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }


    /**
     * 分页查询短链接
     */
    @GetMapping("/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {

        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /**
     * 查询短链接分组内连接数量
     */
    @GetMapping("/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> countPerGroup(@RequestParam("requestParam") List<String> requestParam) {

        return Results.success(shortLinkService.countPerGroup(requestParam));
    }
}
