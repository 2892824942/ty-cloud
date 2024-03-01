package com.ty.mid.framework.cache.configuration;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.ty.mid.framework.cache.condition.CachePlusCondition;
import com.ty.mid.framework.cache.config.CachePlusConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@AutoConfigureBefore({CacheAutoConfiguration.class})
@ConditionalOnClass({Caffeine.class, CaffeineCacheManager.class})
@Import({CachePlusConfig.class})
@Conditional(CachePlusCondition.class)
@Slf4j
public class CaffeineCacheConfiguration {
    @Autowired
    CachePlusConfig cachePlusConfig;

    @Bean
    public CaffeineCacheManager caffeineCacheManager(CacheManagerCustomizers customizers,
                                                     ObjectProvider<Caffeine<Object, Object>> caffeine, ObjectProvider<CaffeineSpec> caffeineSpec,
                                                     ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        CachePlusConfig.Caffeine caffeineProperties = cachePlusConfig.getCaffeine();
        CaffeineCacheManager cacheManager = createCacheManager(caffeineProperties, caffeine, caffeineSpec, cacheLoader);
        return customizers.customize(cacheManager);
    }

    private CaffeineCacheManager createCacheManager(CachePlusConfig.Caffeine caffeineProperties,
                                                    ObjectProvider<Caffeine<Object, Object>> caffeine, ObjectProvider<CaffeineSpec> caffeineSpec,
                                                    ObjectProvider<CacheLoader<Object, Object>> cacheLoader) {
        List<String> cacheNameList = caffeineProperties.getCacheNames();
        log.debug("cacheNameList:{}", cacheNameList);
        String[] cacheNames = cacheNameList.toArray(new String[0]);
        log.debug("cacheNames:{}", (Object[]) cacheNames);
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(cacheNames);
        setCacheBuilder(caffeineProperties, Objects.requireNonNull(caffeineSpec.getIfAvailable()), Objects.requireNonNull(caffeine.getIfAvailable()), cacheManager);
        cacheLoader.ifAvailable(cacheManager::setCacheLoader);
        return cacheManager;
    }

    private void setCacheBuilder(CachePlusConfig.Caffeine caffeineProperties, CaffeineSpec caffeineSpec,
                                 Caffeine<Object, Object> caffeine, CaffeineCacheManager cacheManager) {
        String specification = caffeineProperties.getSpec();
        if (StringUtils.hasText(specification)) {
            cacheManager.setCacheSpecification(specification);
        } else if (caffeineSpec != null) {
            cacheManager.setCaffeineSpec(caffeineSpec);
        } else if (caffeine != null) {
            cacheManager.setCaffeine(caffeine);
        }
    }
}
