package com.ty.mid.framework.cache.configuration;

import com.ty.mid.framework.cache.condition.CachePlusCondition;
import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.cache.support.manager.redis.Redis2PCCacheManager;
import com.ty.mid.framework.cache.support.manager.redis.writer.TransactionHashRedisCacheWriter;
import com.ty.mid.framework.cache.support.manager.redis.writer.TransactionRedisCacheWriter;
import com.ty.mid.framework.common.util.GenericsUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 两阶段提交cache redis实现
 * 使用场景:支持事务系统中,存在缓存及mysql在事务中同时使用,且需要满足读写强一致
 * 实现策略:
 * 1.redis写入延迟到事务提交成功后
 * 2.事务期间,为保持Redis数据可重复读,增加事务期间读锁,读取数据时,先从缓存中读取,如果缓存中不存在,则从Redis中读取,并将读取的数据写入缓存
 *
 * @author suyoulaing
 */
@ConditionalOnClass(RedisConnectionFactory.class)
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@EnableConfigurationProperties(CachePlusConfig.class)
@Conditional(CachePlusCondition.class)
@Slf4j
public class Redis2PCCacheConfiguration {

    private static RedisCacheWriter getRedisCacheWriter(RedisConnectionFactory redisConnectionFactory, CachePlusConfig.Redis redisProperties) {
        CachePlusConfig.Redis.StoreType storeType = redisProperties.getStoreType();
        return Objects.isNull(storeType) || CachePlusConfig.Redis.StoreType.KEY_VALUE.equals(storeType) ?
                new TransactionRedisCacheWriter(redisConnectionFactory, redisProperties.getNullValueTimeToLive(), redisProperties.isCacheNullValues())
                : new TransactionHashRedisCacheWriter(redisConnectionFactory, redisProperties.getNullValueTimeToLive(), redisProperties.isCacheNullValues());
    }

    @Bean
    RedisCacheManager redisCacheManager(CachePlusConfig cachePlusConfig, CacheManagerCustomizers cacheManagerCustomizers,
                                        ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
                                        ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                                        RedisConnectionFactory redisConnectionFactory, ResourceLoader resourceLoader) {
        log.debug("RedisCacheManager bean init!!");

        CachePlusConfig.Redis redisProperties = cachePlusConfig.getRedis();

        RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(
                determineConfiguration(redisProperties, redisCacheConfiguration, resourceLoader.getClassLoader()));
        List<String> cacheNames = redisProperties.getCacheNames();
        if (redisProperties.isEnableTransactions()) {
            builder.transactionAware();
        }
        if (redisProperties.isEnableStatistics()) {
            builder.enableStatistics();
        }
        log.debug("RedisCacheManager cacheNames:{}", cacheNames);
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }
        redisCacheManagerBuilderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        Redis2PCCacheManager redis2PCCacheManager;
        try {
            Field cacheWriterField = RedisCacheManagerBuilder.class.getDeclaredField("cacheWriter");
            Field defaultCacheConfigurationField = RedisCacheManagerBuilder.class.getDeclaredField("defaultCacheConfiguration");
            Field initialCachesField = RedisCacheManagerBuilder.class.getDeclaredField("initialCaches");
            Field allowInFlightCacheCreationField = RedisCacheManagerBuilder.class.getDeclaredField("allowInFlightCacheCreation");
            cacheWriterField.setAccessible(true);
            defaultCacheConfigurationField.setAccessible(true);
            initialCachesField.setAccessible(true);
            allowInFlightCacheCreationField.setAccessible(true);
            Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> initialCaches = GenericsUtil.toMap(initialCachesField.get(builder));
            boolean allowInFlightCacheCreation = (boolean) allowInFlightCacheCreationField.get(builder);
            RedisCacheWriter txRedisCacheWriter = getRedisCacheWriter(redisConnectionFactory, redisProperties);

            redis2PCCacheManager = new Redis2PCCacheManager(txRedisCacheWriter, determineConfiguration(redisProperties, redisCacheConfiguration, resourceLoader.getClassLoader()),
                    initialCaches, allowInFlightCacheCreation);
            if (redisProperties.isEnableTransactions()) {
                redis2PCCacheManager.setTransactionAware(true);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("init error:", e);
            throw new RuntimeException("初始化异常");
        }


        RedisCacheManager customize = cacheManagerCustomizers.customize(redis2PCCacheManager);
        log.debug("RedisCacheManager customize cacheNames:{}", customize.getCacheNames());
        return redis2PCCacheManager;
    }

    private org.springframework.data.redis.cache.RedisCacheConfiguration determineConfiguration(
            CachePlusConfig.Redis redisProperties,
            ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
            ClassLoader classLoader) {
        return redisCacheConfiguration.getIfAvailable(() -> createConfiguration(redisProperties, classLoader));
    }

    private org.springframework.data.redis.cache.RedisCacheConfiguration createConfiguration(
            CachePlusConfig.Redis redisProperties, ClassLoader classLoader) {
        org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration
                .defaultCacheConfig();
        config = config.serializeValuesWith(
                SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
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
