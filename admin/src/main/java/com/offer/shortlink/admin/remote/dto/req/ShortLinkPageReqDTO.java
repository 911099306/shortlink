package com.offer.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/12/5
 *
 * 短链接分页请求参数
 **/
@Data
public class ShortLinkPageReqDTO extends Page {

    /**
     * 分组表示
     */
    private String gid;



}
