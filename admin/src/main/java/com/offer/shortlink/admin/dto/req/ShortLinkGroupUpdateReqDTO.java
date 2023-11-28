package com.offer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
@Data
public class ShortLinkGroupUpdateReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;
}
