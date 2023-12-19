package com.ty.mid.framework.mybatisplus.service.config;

import com.ty.mid.framework.core.spring.SpringContextHelper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.cache.CacheManager;
import javax.cache.Caching;

@Slf4j
public class ServiceAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    SpringContextHelper springContextHelper() {
        SpringContextHelper helper = new SpringContextHelper();
        helper.setApplicationContext(applicationContext);
        return helper;
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean
    <K, V> javax.cache.configuration.Configuration<K, V> configuration(RedissonClient redisClient) {
        return RedissonConfiguration.fromInstance(redisClient);
    }


    @Bean
    @ConditionalOnMissingBean
    CacheManager jCacheCacheManager() {
        return Caching.getCachingProvider().getCacheManager();
    }


}
