package com.offer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.admin.dao.entity.GroupDO;
import com.offer.shortlink.admin.dao.mapper.GroupMapper;
import com.offer.shortlink.admin.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO>
        implements GroupService {


}
