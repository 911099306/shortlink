package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.ShortLinkDO;
import com.offer.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;

import java.util.List;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/29
 * 短链接持久层
 **/
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {
    /**
     * 以 gid 为组，查询每个gid下的短链接数量
     * @param requestParam  gid 的集合
     */
    List<ShortLinkGroupCountQueryRespDTO> countPerGroup(List<String> requestParam);
}
