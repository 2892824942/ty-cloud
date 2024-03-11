package com.ty.mid.framework.cache.configuration.base;

import com.ty.mid.framework.cache.configuration.*;
import com.ty.mid.framework.cache.constant.CachePlusType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mappings between {@link CachePlusType} and {@code @Configuration}.
 * If you want expand your cache：
 * 1.
 *
 * @author Phillip Webb
 * @author Eddú Meléndez
 */
public class CacheConfigurations {

    public static final Map<String, Class<?>> MAPPINGS;

    static {
        //spring官方
        Map<String, Class<?>> mappings = new HashMap<>();
        mappings.put(CachePlusType.GENERIC.name(), GenericCacheConfiguration.class);

//        mappings.put(CachePlusType.INFINISPAN.name(), InfinispanCacheConfiguration.class);
//
//        mappings.put(CachePlusType.COUCHBASE.name(), CouchbaseCacheConfiguration.class);
        mappings.put(CachePlusType.REDIS.name(), RedisCacheConfiguration.class);
        mappings.put(CachePlusType.CAFFEINE.name(), CaffeineCacheConfiguration.class);
        mappings.put(CachePlusType.SIMPLE.name(), SimpleCacheConfiguration.class);
        mappings.put(CachePlusType.NONE.name(), NoOpCacheConfiguration.class);
        mappings.put(CachePlusType.JCACHE.name(), JCacheCacheConfiguration.class);
//        mappings.put(CacheSuperType.EHCACHE, EhCacheCacheConfiguration.class);
//        mappings.put(CacheSuperType.HAZELCAST, HazelcastCacheConfiguration.class);
        //自定义实现
        mappings.put(CachePlusType.REDISSON.name(), RedissonCacheConfiguration.class);
        mappings.put(CachePlusType.REDISSON_2PC.name(), Redis2PCCacheConfiguration.class);
        mappings.put(CachePlusType.REDISSON_LOCAL_MAP.name(), RedissonRLocalMapCacheConfiguration.class);
        MAPPINGS = Collections.unmodifiableMap(mappings);
    }

    private CacheConfigurations() {
    }

    public static CachePlusType getType(String configurationClassName) {
        for (Map.Entry<String, Class<?>> entry : MAPPINGS.entrySet()) {
            if (entry.getValue().getName().equals(configurationClassName)) {
                return CachePlusType.valueOf(entry.getKey());
            }
        }
        throw new IllegalStateException("Unknown configuration class " + configurationClassName);
    }

}
