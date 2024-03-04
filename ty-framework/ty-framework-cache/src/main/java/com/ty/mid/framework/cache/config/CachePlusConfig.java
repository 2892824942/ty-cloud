package com.ty.mid.framework.cache.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.ty.mid.framework.cache.config.redisson.LocalCachedMapOptions;
import com.ty.mid.framework.cache.constant.CachePlusType;
import com.ty.mid.framework.core.config.AbstractConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.spring.cache.CacheConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = CachePlusConfig.CACHE_PREFIX)
@Data
@Slf4j
public class CachePlusConfig extends AbstractConfig {
    public static final String CACHE_PREFIX = FRAMEWORK_PREFIX + "cache";
    public static final String CACHE_MULTI_ENABLE = "multi-enable";
    //本地数据缓存，方便处理，非配置类
    private static final Map<CachePlusType, BaseConfig> typeConfigMap = new HashMap<>();
    final List<CachePlusType> type = new ArrayList<>();
    private final Caffeine caffeine = new Caffeine();
    private final Couchbase couchbase = new Couchbase();
    private final EhCache ehcache = new EhCache();
    private final Infinispan infinispan = new Infinispan();
    private final JCache jcache = new JCache();
    private final Redis redis = new Redis();
    private final RedissonDefault redisson = new RedissonDefault();
    private final Redis redissonRLocalMap = new RedissonLocalMap();
    protected boolean multiEnable;
    private CustomizeCacheConfig customize = new CustomizeCacheConfig();

    public Map<String, CacheConfig> getRedisConfig(CachePlusType cachePlusType) {
        Redis redis = this.getCacheProperties(cachePlusType);
        Map<String, CacheConfig> redisConfig = new HashMap<>();
        log.debug("getRedisConfig:{},cachePlusType:{}", this.getTypeNameMap(), cachePlusType);
        this.getTypeNameMap().get(cachePlusType).forEach(cacheName -> {
            if (redis.isUseKeyPrefix() && StringUtils.isNotEmpty(redis.getKeyPrefix())) {
                cacheName = redis.getKeyPrefix().concat("-");
            }
            Duration timeToLive = redis.getTimeToLive();
            if (Objects.nonNull(timeToLive)) {
                long ttl = timeToLive.toMillis();
                redisConfig.put(cacheName, new CacheConfig(ttl, ttl / 2));
            }else {
                redisConfig.put(cacheName, new CacheConfig());
            }
        });
        return redisConfig;
    }

    public LocalCachedMapOptions<Object, Object> getRedissonLocalMapConfig() {
        LocalCachedMapOptions<Object, Object> localCached = this.getCustomize().getLocalCached();
        return Optional.ofNullable(localCached).orElseGet(LocalCachedMapOptions::defaults);
    }

    public Map<CachePlusType, List<String>> getTypeNameMap() {
        return typeConfigMap.values().stream().collect(Collectors.toMap(BaseConfig::getType, BaseConfig::getCacheNames));
    }


    public <T extends BaseConfig> T getCacheProperties(CachePlusType cachePlusType) {
        return Convert.convert(new TypeReference<T>() {
        }, typeConfigMap.get(cachePlusType));
    }


    /**
     * Resolve the config location if set.
     *
     * @param config the config resource
     * @return the location or {@code null} if it is not set
     * @throws IllegalArgumentException if the config attribute is set to an unknown
     *                                  location
     */
    public Resource resolveConfigLocation(Resource config) {
        if (config != null) {
            Assert.isTrue(config.exists(),
                    () -> "Cache configuration does not exist '" + config.getDescription() + "'");
            return config;
        }
        return null;
    }

    /**
     * Caffeine specific cache properties.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Caffeine extends BaseConfig {

        /**
         * The spec to use to create caches. See CaffeineSpec for more details on the spec
         * format.
         */
        private String spec;


        @Override
        protected CachePlusType getType() {
            return CachePlusType.CAFFEINE;
        }
    }

    /**
     * Couchbase specific cache properties.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Couchbase extends BaseConfig {

        /**
         * Entry expiration. By default the entries never expire. Note that this value is
         * ultimately converted to seconds.
         */
        private Duration expiration;


        @Override
        protected CachePlusType getType() {
            return CachePlusType.COUCHBASE;
        }
    }

    /**
     * EhCache specific cache properties.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class EhCache extends BaseConfig {

        /**
         * The location of the configuration file to use to initialize EhCache.
         */
        private Resource config;

        @Override
        protected CachePlusType getType() {
            return CachePlusType.EHCACHE;
        }
    }

    /**
     * Infinispan specific cache properties.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Infinispan extends BaseConfig {

        /**
         * The location of the configuration file to use to initialize Infinispan.
         */
        private Resource config;

        @Override
        protected CachePlusType getType() {
            return CachePlusType.INFINISPAN;
        }
    }

    /**
     * JCache (JSR-107) specific cache properties.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class JCache extends BaseConfig {

        /**
         * The location of the configuration file to use to initialize the cache manager.
         * The configuration file is dependent of the underlying cache implementation.
         */
        private Resource config;

        /**
         * Fully qualified name of the CachingProvider implementation to use to retrieve
         * the JSR-107 compliant cache manager. Needed only if more than one JSR-107
         * implementation is available on the classpath.
         */
        private String provider;


        @Override
        protected CachePlusType getType() {
            return CachePlusType.JCACHE;
        }
    }

    /**
     * Redis-specific cache properties.
     */

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class RedissonDefault extends Redis {
        @Override
        protected CachePlusType getType() {
            return CachePlusType.REDISSON_LOCAL_MAP;
        }
    }


    /**
     * Redis-specific cache properties.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class RedissonLocalMap extends Redis {
        @Override
        protected CachePlusType getType() {
            return CachePlusType.REDISSON_2PC;
        }
    }

    /**
     * Redis-specific cache properties.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Redis extends TxBaseConfig {

        private StoreType storeType = StoreType.KEY_VALUE;
        /**
         * Entry expiration. By default the entries never expire.
         */
        private Duration nullValueTimeToLive;
        /**
         * Entry expiration. By default the entries never expire.
         */
        private Duration timeToLive;
        /**
         * Allow caching null values.
         */
        private boolean cacheNullValues = false;
        /**
         * Key prefix.
         */
        private String keyPrefix;
        /**
         * Whether to use the key prefix when writing to Redis.
         */
        private boolean useKeyPrefix = true;

        @Override
        protected CachePlusType getType() {
            return CachePlusType.REDIS;
        }

        public enum StoreType {
            /**
             * 普通键值对
             */
            KEY_VALUE,

            /**
             * Hash格式
             */
            HASH,
            ;
        }
    }

    @Data
    public static abstract class BaseConfig {
        List<String> cacheNames = new ArrayList<>();

        public BaseConfig() {
            typeConfigMap.put(getType(), this);
        }

        protected abstract CachePlusType getType();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static abstract class TxBaseConfig extends BaseConfig {
        boolean enableTransactions;
    }

}
