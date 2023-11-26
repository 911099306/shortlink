package com.offer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.offer.shortlink.admin.dao.entity.UserDo;
import com.offer.shortlink.admin.dto.resp.UserRespDTO;

/**
* @author serendipity
* @description 针对表【t_user】的数据库操作Service
* @createDate 2023-11-26 18:01:54
*/
public interface UserService extends IService<UserDo> {

    /**
     * 根据用户名查询用户信息
     * @param username  用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);
}
