package com.offer.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.offer.shortlink.project.dao.entity.ShortLinkDO;
import com.offer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/29
 **/
public interface ShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短连接信息
     * @param requestParam 创建短链接请求参数
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
