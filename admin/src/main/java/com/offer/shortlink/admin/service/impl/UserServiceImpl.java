package com.offer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.admin.common.convention.exception.ClientException;
import com.offer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.offer.shortlink.admin.dao.entity.UserDo;
import com.offer.shortlink.admin.dao.mapper.UserMapper;
import com.offer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.offer.shortlink.admin.dto.resp.UserRespDTO;
import com.offer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import static com.offer.shortlink.admin.common.constant.RedissonCacheConstant.LOCK_USER_REGISTER_KET;
import static com.offer.shortlink.admin.common.enums.UserErrorCodeEnum.*;

/**
* @author serendipity
* @description 针对表【t_user】的数据库操作Service实现
* @createDate 2023-11-26 18:01:54
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo>
    implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;

    @Override
    public UserRespDTO getUserByUsername(String username) {

        UserDo userDo = this.lambdaQuery()
                .eq(UserDo::getUsername, username)
                .one();
        if (userDo == null) {
            throw new  ClientException(UserErrorCodeEnum.USER_NULL);
        }
        return BeanUtil.copyProperties(userDo,UserRespDTO.class);
    }

    @Override
    public Boolean hasUsername(String username) {

        // return !this.lambdaQuery()
        //         .eq(UserDo::getUsername, username)
        //         .exists();

        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    /**
     * 用户注册请求
     * @param requestParam 用户注册请求参数
     * @return
     */
    @Override
    public void register(UserRegisterReqDTO requestParam) {
        // 1. 判断 用户名 是否可用
        if (hasUsername(requestParam.getUsername())) {
            // 不可用， 直接抛出异常
            throw new ClientException(USER_NAME_EXIST);
        }

        // 2. 可用， 添加至数据库
        // 使用 分布式锁 防止海量请求攻击
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KET + requestParam.getUsername());
        try {
            if (lock.tryLock()) {
                UserDo userDo = BeanUtil.copyProperties(requestParam, UserDo.class);
                boolean save = this.save(userDo);

                if (!save) {
                    throw new ClientException(USER_SAVE_ERROR);
                }

                // 3. 添加数据库成功， 加入布隆过滤器
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            }

            // 获取 锁 失败, 说明已经有线程创建了
            throw new ClientException(USER_NAME_EXIST);
        } finally {
            lock.unlock();
        }


    }
}




