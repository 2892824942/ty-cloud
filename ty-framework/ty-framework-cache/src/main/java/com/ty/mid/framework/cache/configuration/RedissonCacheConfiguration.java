package com.ty.mid.framework.cache.configuration;

import com.ty.mid.framework.cache.condition.CachePlusCondition;
import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.cache.constant.CachePlusType;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.util.Map;

@AutoConfigureBefore({CacheAutoConfiguration.class})
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@EnableConfigurationProperties(CachePlusConfig.class)
@Conditional(CachePlusCondition.class)
public class RedissonCacheConfiguration {

    @Autowired
    CachePlusConfig multiCacheConfig;

    /**
     * Redisson 官方提供的CacheManager
     */
    @Bean
    RedissonSpringCacheManager redissonDefaultCacheManage(RedissonClient redissonClient, CacheManagerCustomizers cacheManagerCustomizers) {
        Map<String, CacheConfig> redisConfig = multiCacheConfig.getRedisConfig(CachePlusType.REDIS);
        RedissonSpringCacheManager redissonSpringCacheManager = new RedissonSpringCacheManager(redissonClient, redisConfig);
        cacheManagerCustomizers.customize(redissonSpringCacheManager);
        return redissonSpringCacheManager;
    }

}
