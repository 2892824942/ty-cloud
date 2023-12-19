package com.ty.mid.framework.cache.configration;

import com.ty.mid.framework.cache.condition.CachePlusCondition;
import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.cache.support.manager.redis.Redis2PCCacheManager;
import com.ty.mid.framework.cache.support.manager.redis.writer.TransactionHashRedisCacheWriter;
import com.ty.mid.framework.cache.support.manager.redis.writer.TransactionRedisCacheWriter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;
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
 * Redis cache configuration.
 *
 * @author Stephane Nicoll
 * @author Mark Paluch
 * @author Ryon Day
 */
@ConditionalOnClass(RedisConnectionFactory.class)
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@Import({CachePlusConfig.class})
@Conditional(CachePlusCondition.class)
@Slf4j
public class RedisCacheConfiguration {

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
            Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> initialCaches = (Map) initialCachesField.get(builder);
            boolean allowInFlightCacheCreation = (boolean) allowInFlightCacheCreationField.get(builder);
            CachePlusConfig.Redis.StroeType stroeType = redisProperties.getStroeType();
            RedisCacheWriter txRedisCacheWriter = Objects.isNull(stroeType) || CachePlusConfig.Redis.StroeType.KEY_VALUE.equals(stroeType) ?
                    new TransactionRedisCacheWriter(redisConnectionFactory, redisProperties.getNullValueTimeToLive(), redisProperties.isCacheNullValues())
                    : new TransactionHashRedisCacheWriter(redisConnectionFactory, redisProperties.getNullValueTimeToLive(), redisProperties.isCacheNullValues());

            redis2PCCacheManager = new Redis2PCCacheManager(txRedisCacheWriter, determineConfiguration(redisProperties, redisCacheConfiguration, resourceLoader.getClassLoader()),
                    initialCaches, allowInFlightCacheCreation);
            if (redisProperties.isEnableTransactions()) {
                redis2PCCacheManager.setTransactionAware(true);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
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
