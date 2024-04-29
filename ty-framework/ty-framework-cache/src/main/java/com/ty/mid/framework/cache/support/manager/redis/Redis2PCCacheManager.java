package com.ty.mid.framework.cache.support.manager.redis;

import com.ty.mid.framework.cache.support.cache.GetIfAbsentRedisCache;
import com.ty.mid.framework.cache.support.manager.decorator.CustomTransactionAwareCacheDecorator;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.Map;

/**
 * Redis 两阶段提交cacheManager实现 <p>
 * 整合Mysql事务,事务支持
 */
public class Redis2PCCacheManager extends RedisCacheManager {
    RedisCacheWriter cacheWriter;

    public Redis2PCCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
        this.cacheWriter = cacheWriter;
    }


    @Override
    protected Cache decorateCache(Cache cache) {
        return (isTransactionAware() ? new CustomTransactionAwareCacheDecorator(cache) : cache);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        return new GetIfAbsentRedisCache(name, cacheWriter, cacheConfig != null ? cacheConfig : RedisCacheConfiguration.defaultCacheConfig());
    }
}
