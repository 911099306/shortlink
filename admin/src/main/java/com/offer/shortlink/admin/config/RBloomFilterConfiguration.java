package com.offer.shortlink.admin.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27 13:34
 *
 * 布隆过滤器配置
 **/
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 防止用户注册查询数据库的布隆过滤器
     * @param redissonClient
     * @return
     */
    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(100000000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}
