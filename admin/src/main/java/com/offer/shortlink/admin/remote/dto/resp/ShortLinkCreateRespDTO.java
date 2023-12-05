package com.offer.shortlink.admin.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/29
 *
 * 短链接创建返回对象
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkCreateRespDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 短链接
     */
    private String fullShortUrl;


}
