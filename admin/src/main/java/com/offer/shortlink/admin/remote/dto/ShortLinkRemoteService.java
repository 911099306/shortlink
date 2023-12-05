package com.offer.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/12/5
 *
 * 短链接中台远程调用服务
 **/
public interface ShortLinkRemoteService {


    /**
     * 创建短链接
     * @param requestParam 创建短链接请求参数
     * @return 创建短链接响应
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        String resultBodyStr
                = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 分页查询请求
     * @param requestParam 分页查询请求参数
     * @return 分页查询请求响应
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        // 使用 hutool 直接调用http接口

        // 因为是 GET 请求，map 可以直接拼接为参数进行接收
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());

        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }



}
