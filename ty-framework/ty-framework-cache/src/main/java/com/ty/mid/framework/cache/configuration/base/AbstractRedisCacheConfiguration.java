package com.ty.mid.framework.cache.configuration.base;

import cn.hutool.core.util.ReflectUtil;
import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.cache.constant.CachePlusType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;


public abstract class AbstractRedisCacheConfiguration {
    @Resource
    protected CachePlusConfig cachePlusConfig;

    protected RedisCacheManagerBuilder handleBuilder(CachePlusType cachePlusType,
                                                     ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
                                                     RedisConnectionFactory redisConnectionFactory, ResourceLoader resourceLoader) {
        CachePlusConfig.Redis cacheProperties = cachePlusConfig.getCacheProperties(cachePlusType);
        RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(determineConfiguration(cachePlusConfig.getRedis(), redisCacheConfiguration, resourceLoader.getClassLoader()));
        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }
        if (cacheProperties.isEnableStatistics()) {
            builder.enableStatistics();
        }
        if (cacheProperties.isEnableTransactions()) {
            builder.transactionAware();
        }
        return builder;
    }

    protected org.springframework.data.redis.cache.RedisCacheConfiguration determineConfiguration(
            CachePlusConfig.Redis redisProperties,
            ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
            ClassLoader classLoader) {
        return redisCacheConfiguration.getIfAvailable(() -> createConfiguration(redisProperties, classLoader));
    }

    protected org.springframework.data.redis.cache.RedisCacheConfiguration createConfiguration(
            CachePlusConfig.Redis redisProperties, ClassLoader classLoader) {
        org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration
                .defaultCacheConfig(classLoader);
        config = config
                .serializeValuesWith(SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class)));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config.usePrefix();
            Field keyPrefix = ReflectUtil.getField(config.getClass(), "keyPrefix");
            Field field = ReflectUtil.setAccessible(keyPrefix);
            try {
                field.set(config, (CacheKeyPrefix) cacheName -> redisProperties.getKeyPrefix().concat(":").concat(cacheName));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return config;
    }

}
