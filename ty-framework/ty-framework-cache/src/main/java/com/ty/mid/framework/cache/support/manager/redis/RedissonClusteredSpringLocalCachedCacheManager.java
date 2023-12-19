package com.ty.mid.framework.cache.support.manager.redis;

import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;

import java.util.Map;
import java.util.Objects;

/**
 * 重写RedissonSpringCacheManager，缓存使用带本地缓存的RLocalCacheMap
 */
public class RedissonClusteredSpringLocalCachedCacheManager extends RedissonSpringCacheManager {
    RedissonClient redisson;
    private Codec codec;
    private LocalCachedMapOptions<Object, Object> localCachedMapOptions = null;


    public RedissonClusteredSpringLocalCachedCacheManager(RedissonClient redisson) {
        super(redisson);
        this.redisson = redisson;

    }

    public RedissonClusteredSpringLocalCachedCacheManager(RedissonClient redisson, LocalCachedMapOptions<Object, Object> localCachedMapOptions) {
        this(redisson, (String) null, localCachedMapOptions);
        this.redisson = redisson;

    }

    public RedissonClusteredSpringLocalCachedCacheManager(RedissonClient redisson, Map<String, ? extends CacheConfig> config) {
        this(redisson, config, null, null);
        this.redisson = redisson;

    }

    public RedissonClusteredSpringLocalCachedCacheManager(RedissonClient redisson, Map<String, ? extends CacheConfig> config, LocalCachedMapOptions<Object, Object> localCachedMapOptions) {
        this(redisson, config, null, localCachedMapOptions);

    }

    public RedissonClusteredSpringLocalCachedCacheManager(RedissonClient redisson, Map<String, ? extends CacheConfig> config, Codec codec, LocalCachedMapOptions<Object, Object> localCachedMapOptions) {
        super(redisson, config, codec);
        this.redisson = redisson;
        this.codec = codec;
        this.localCachedMapOptions = localCachedMapOptions;
    }

    public RedissonClusteredSpringLocalCachedCacheManager(RedissonClient redisson, String configLocation, LocalCachedMapOptions<Object, Object> localCachedMapOptions) {
        this(redisson, configLocation, null, localCachedMapOptions);
        this.redisson = redisson;
    }

    public RedissonClusteredSpringLocalCachedCacheManager(RedissonClient redisson, String configLocation, Codec codec, LocalCachedMapOptions<Object, Object> localCachedMapOptions) {
        super(redisson, configLocation, codec);
        this.localCachedMapOptions = localCachedMapOptions;
    }


    @Override
    protected RMap<Object, Object> getMap(String name, CacheConfig config) {
        //获取配置
        LocalCachedMapOptions<Object, Object> reaLocalCachedMapOptions = Objects.nonNull(localCachedMapOptions) ? localCachedMapOptions : LocalCachedMapOptions.defaults();
        if (codec != null) {
            return redisson.getLocalCachedMap(name, codec, reaLocalCachedMapOptions);
        }
        return redisson.getLocalCachedMap(name, reaLocalCachedMapOptions);
    }
}
