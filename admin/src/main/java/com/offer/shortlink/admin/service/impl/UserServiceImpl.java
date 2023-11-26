package com.offer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.admin.common.convention.exception.ClientException;
import com.offer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.offer.shortlink.admin.dao.entity.UserDo;
import com.offer.shortlink.admin.dao.mapper.UserMapper;
import com.offer.shortlink.admin.dto.resp.UserRespDTO;
import com.offer.shortlink.admin.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author serendipity
* @description 针对表【t_user】的数据库操作Service实现
* @createDate 2023-11-26 18:01:54
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo>
    implements UserService {

    @Override
    public UserRespDTO getUserByUsername(String username) {

        UserDo userDo = this.lambdaQuery()
                .eq(UserDo::getUsername, username)
                .one();
        if (userDo == null) {
            throw new  ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDo, result);
        return result;
    }
}




