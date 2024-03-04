package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.cache.support.MultiCacheManagerKeeper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.stream.Collectors;

@AutoConfigureBefore(CacheAutoConfiguration.class)
@EnableConfigurationProperties(CachePlusConfig.class)
@ConditionalOnProperty(prefix = CachePlusConfig.CACHE_PREFIX, name = CachePlusConfig.CACHE_MULTI_ENABLE, havingValue = "true")
@Slf4j
public class MultiCacheAutoConfiguration {
    @Bean
    @Primary
    public MultiCacheManagerKeeper cacheManager(CachePlusConfig cacheConfig, ObjectProvider<CacheManager> cacheManagers) {
        return new MultiCacheManagerKeeper(cacheConfig, cacheManagers);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<CacheManagerCustomizer<?>> customizers) {
        return new CacheManagerCustomizers(customizers.orderedStream().collect(Collectors.toList()));
    }

}
