package com.ty.mid.framework.cache.configuration.base;


import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.cache.config.redisson.LocalCachedMapOptions;
import com.ty.mid.framework.cache.constant.CachePlusType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.spring.cache.CacheConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AbstractRedissonCacheConfiguration {
    @Autowired
    protected CachePlusConfig cachePlusConfig;

    public Map<String, CacheConfig> getRedissonConfig(CachePlusType cachePlusType) {
        CachePlusConfig.Redis redis = cachePlusConfig.getCacheProperties(cachePlusType);
        Map<String, CacheConfig> redisConfig = new HashMap<>();
        log.debug("getRedisConfig:{},cachePlusType:{}", CachePlusConfig.getTypeNameMap(), cachePlusType);
        CachePlusConfig.getTypeNameMap().get(cachePlusType).forEach(cacheName -> {
            if (redis.isUseKeyPrefix() && StringUtils.isNotEmpty(redis.getKeyPrefix())) {
                cacheName = redis.getKeyPrefix().concat("-");
            }
            //TTl的配置这里可以设计成每个缓存独立,但是这样很麻烦,配置起来很烦,我觉得这个地方作为全局默认合适,对于每个缓存个性失效,应放在注解更合适
            Duration timeToLive = redis.getTimeToLive();
            if (Objects.nonNull(timeToLive)) {
                long ttl = timeToLive.toMillis();
                redisConfig.put(cacheName, new CacheConfig(ttl, ttl / 2));
            } else {
                redisConfig.put(cacheName, new CacheConfig());
            }
        });
        return redisConfig;
    }

    public LocalCachedMapOptions<Object, Object> getRedissonLocalMapConfig() {
        LocalCachedMapOptions<Object, Object> localCached = cachePlusConfig.getCustomize().getLocalCached();
        return Optional.ofNullable(localCached).orElseGet(LocalCachedMapOptions::defaults);
    }

}
