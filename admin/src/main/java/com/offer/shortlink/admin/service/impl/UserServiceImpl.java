package com.offer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.admin.common.convention.exception.ClientException;
import com.offer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.offer.shortlink.admin.dao.entity.UserDo;
import com.offer.shortlink.admin.dao.mapper.UserMapper;
import com.offer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.offer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.offer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.offer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.offer.shortlink.admin.dto.resp.UserRespDTO;
import com.offer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private final StringRedisTemplate stringRedisTemplate;

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

    /**
     * 根据 username 修改用户信息
     * @param requestParam
     * @return
     */
    @Override
    public void update(UserUpdateReqDTO requestParam) {

        // TODO 验证当前用户名是否为登录用户

        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.<UserDo>lambdaQuery()
                .eq(UserDo::getUsername, requestParam.getUsername());
        this.update(BeanUtil.copyProperties(requestParam, UserDo.class), queryWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {

        // 根据用户名查询信息
        UserDo userDo = this.lambdaQuery()
                .eq(UserDo::getUsername, requestParam.getUsername())
                .eq(UserDo::getDelFlag,0)
                .one();
        // 记录不存在，抛异常
        if (userDo == null) {
            throw new ClientException(USER_NULL);
        }

        if (!userDo.getPassword().equals(requestParam.getPassword())) {
            throw new ClientException(USER_PASSWORD_ERROR);
        }

     /*   防止同一账号多次登录,使用redis 缓存进行保护
        redis 存储 hash 结构数据
        Key: Login_userId
        Value:
          key: uuid
          value: JSON(用户信息）*/
        String key = "login_" + requestParam.getUsername();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            throw new ClientException("用户已登录");
        }


        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(key, uuid, JSON.toJSONString(userDo));
        stringRedisTemplate.expire(key, 30L, TimeUnit.MINUTES);
        // 返回用户信息
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username,String token) {

        return stringRedisTemplate.opsForHash().hasKey("login_" + username, token);
    }
}




