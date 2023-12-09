package com.offer.shortlink.admin.dto.resp;

import lombok.Data;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 *
 * 短链接分组返回实体对象
 **/
@Data
public class ShortLinkGroupRespDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}
