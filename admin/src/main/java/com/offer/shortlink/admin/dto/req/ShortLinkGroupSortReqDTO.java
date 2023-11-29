package com.offer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
@Data
public class ShortLinkGroupSortReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 排序字段
     */
    private Long sortOrder;
}
