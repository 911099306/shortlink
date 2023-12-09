package com.offer.shortlink.project.dto.resp;

import lombok.Builder;
import lombok.Data;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/12/9
 *
 * 某个分组内连接数量
 **/
@Data
@Builder
public class ShortLinkGroupCountQueryRespDTO {

    /**
     * 短链接分组唯一标识
     */
    private String gid;

    /**
     * 分组内连接总数
     */
    private Integer shortLinkCount;
}
