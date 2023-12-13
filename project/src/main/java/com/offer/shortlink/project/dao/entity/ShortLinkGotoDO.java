package com.offer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/12/13
 **/
@Data
@Builder
@TableName("t_link_goto")
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkGotoDO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
