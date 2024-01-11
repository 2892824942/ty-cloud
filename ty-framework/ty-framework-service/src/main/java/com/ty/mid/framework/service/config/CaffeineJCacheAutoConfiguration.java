package com.ty.mid.framework.service.config;

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@ConditionalOnClass(CaffeineCachingProvider.class)
public class CaffeineJCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JCacheCacheManager jCacheCacheManager() {
        // 获取 Caffeine 的 CachingProvider
        javax.cache.spi.CachingProvider cachingProvider = Caching.getCachingProvider(CaffeineCachingProvider.class.getName());

        // 创建 Caffeine CacheManager
        CacheManager cacheManager = cachingProvider.getCacheManager();
        return new JCacheCacheManager(cacheManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public Configuration<?, ?> configuration() {
        // 创建 Caffeine 缓存配置
        Configuration<Object, Object> caffeineConfig = new MutableConfiguration<>()
                .setTypes(Object.class, Object.class)
                // 设置为 true 时，缓存的值会被拷贝而不是直接引用
                .setStoreByValue(false)
                .setReadThrough(true)
                // 设置失效时间
                .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.DAYS, 1)))
                .setStatisticsEnabled(Boolean.TRUE);
        return caffeineConfig;
    }


}
