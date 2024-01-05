package com.ty.mid.framework.mybatisplus.service.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Slf4j
@ConditionalOnClass(RedissonClient.class)
public class RedisJCacheAutoConfiguration {


    @Bean
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean
    <K, V> javax.cache.configuration.Configuration<K, V> configuration(RedissonClient redisClient) {
        return RedissonConfiguration.fromInstance(redisClient);
    }


}
