package com.ty.mid.framework.cache.configuration;

import com.ty.mid.framework.cache.condition.CachePlusCondition;
import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.cache.config.redisson.LocalCachedMapOptions;
import com.ty.mid.framework.cache.constant.CachePlusType;
import com.ty.mid.framework.cache.support.manager.redis.RedissonClusteredSpringLocalCachedCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

import java.util.Map;

@AutoConfigureBefore({CacheAutoConfiguration.class})
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@EnableConfigurationProperties(CachePlusConfig.class)
@Import({CacheConfig.class})
@Conditional(CachePlusCondition.class)
@Slf4j
public class RedissonRLocalMapCacheConfiguration {

    @Autowired
    CachePlusConfig cachePlusConfig;

    /**
     * 重写 Redisson官方提供的CacheManager  基于RLocalMapCache实现
     */
    @Bean
    RedissonClusteredSpringLocalCachedCacheManager redissonCustomerCacheManage(RedissonClient redissonClient, CacheManagerCustomizers cacheManagerCustomizers) {
        Map<String, CacheConfig> redisConfig = cachePlusConfig.getRedisConfig(CachePlusType.REDISSON_LOCAL_MAP);
        LocalCachedMapOptions<Object, Object> redissonLocalMapConfig = cachePlusConfig.getRedissonLocalMapConfig();
        org.redisson.api.LocalCachedMapOptions<Object, Object> defaults = org.redisson.api.LocalCachedMapOptions.defaults();
        BeanUtils.copyProperties(redissonLocalMapConfig, LocalCachedMapOptions.defaults());
        RedissonClusteredSpringLocalCachedCacheManager redissonSpringCacheManager =
                new RedissonClusteredSpringLocalCachedCacheManager(redissonClient, redisConfig, defaults);
        redissonSpringCacheManager.setCacheNames(cachePlusConfig.getRedissonRLocalMap().getCacheNames());

        cacheManagerCustomizers.customize(redissonSpringCacheManager);
        return redissonSpringCacheManager;
    }

}
