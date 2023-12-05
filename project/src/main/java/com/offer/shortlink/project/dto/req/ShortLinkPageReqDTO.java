package com.offer.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.offer.shortlink.project.dao.entity.ShortLinkDO;
import lombok.Data;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/12/5
 *
 * 短链接分页请求参数
 **/
@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {

    /**
     * 分组表示
     */
    private String gid;



}
