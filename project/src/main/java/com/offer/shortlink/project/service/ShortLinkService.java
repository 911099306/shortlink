package com.offer.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.offer.shortlink.project.dao.entity.ShortLinkDO;
import com.offer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

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

    /**
     * 修改短链接信息
     * @param requestParam
     * @return
     *
     *
     *     "validDateType": 65,
     *
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 短链接分页查询
     * @param requestParam 短链接分页查询参数
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 查询分组内的连接数量
     */
    List<ShortLinkGroupCountQueryRespDTO> countPerGroup(List<String> requestParams);


}
