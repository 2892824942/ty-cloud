package com.ty.mid.framework.cache.configuration;

import com.ty.mid.framework.cache.condition.CachePlusCondition;
import com.ty.mid.framework.cache.config.CachePlusConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * 暂时还不完善，不可使用 <p>
 *
 * @author Stephane Nicoll <p>
 * @author Madhura Bhave
 */
@ConditionalOnClass({Caching.class, JCacheCacheManager.class})
@EnableConfigurationProperties(CachePlusConfig.class)
@Conditional({CachePlusCondition.class})
@Slf4j
public class JCacheCacheConfiguration implements BeanClassLoaderAware {

    private ClassLoader beanClassLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    //以下两个bean是测试
    @Bean
    @ConditionalOnClass(RedisClient.class)
    @ConditionalOnMissingBean
    <K, V> javax.cache.configuration.Configuration<K, V> configuration(RedissonClient redisClient) {
        log.debug("JCacheCacheManager  configuration is init!!");
        return RedissonConfiguration.fromInstance(redisClient);
    }

    @Bean
    javax.cache.CacheManager jCacheCacheManager() {
        log.debug("JCacheCacheManager is init!!");
        return Caching.getCachingProvider().getCacheManager();
    }

    @Bean
    org.springframework.cache.CacheManager jCacheManager(CacheManagerCustomizers customizers, javax.cache.CacheManager jCacheCacheManager, ObjectProvider<javax.cache.configuration.Configuration<?, ?>> defaultCacheConfiguration) {
        log.debug("Spring JCacheCacheManager is init!!");
        JCacheCacheManager cacheManager = new JCacheCacheManager(jCacheCacheManager);
        return customizers.customize(cacheManager);
    }

//    @Bean("jCacheCacheManager")
//    @ConditionalOnMissingBean
//    CacheManager jCacheCacheManager(CachePlusConfig cachePlusConfig,
//                                    ObjectProvider<javax.cache.configuration.Configuration<?, ?>> defaultCacheConfiguration,
//                                    ObjectProvider<JCacheManagerCustomizer> cacheManagerCustomizers) throws IOException {
//        log.debug("Spring JCacheCacheManager is init!!");
//        CachePlusConfig.JCache cacheProperties = cachePlusConfig.getJcache();
//        CacheManager jCacheCacheManager = createCacheManager(cachePlusConfig);
//        List<String> cacheNames = cacheProperties.getCacheNames();
//        if (!CollectionUtils.isEmpty(cacheNames)) {
//            for (String cacheName : cacheNames) {
//                jCacheCacheManager.createCache(cacheName,
//                        defaultCacheConfiguration.getIfAvailable(MutableConfiguration::new));
//            }
//        }
//        cacheManagerCustomizers.orderedStream().forEach((customizer) -> customizer.customize(jCacheCacheManager));
//        return jCacheCacheManager;
//    }

    private CacheManager createCacheManager(CachePlusConfig cachePlusConfig) throws IOException {
        CachingProvider cachingProvider = getCachingProvider(cachePlusConfig.getJcache().getProvider());
        Properties properties = createCacheManagerProperties(cachePlusConfig);
        Resource configLocation = cachePlusConfig.resolveConfigLocation(cachePlusConfig.getJcache().getConfig());
        if (configLocation != null) {
            return cachingProvider.getCacheManager(configLocation.getURI(), this.beanClassLoader, properties);
        }
        return cachingProvider.getCacheManager(null, this.beanClassLoader, properties);
    }

    private CachingProvider getCachingProvider(String cachingProviderFqn) {
        if (StringUtils.hasText(cachingProviderFqn)) {
            return Caching.getCachingProvider(cachingProviderFqn);
        }
        return Caching.getCachingProvider();
    }

    private Properties createCacheManagerProperties(CachePlusConfig cachePlusConfig) {
        Properties properties = new Properties();
        //TODO 这里没有做兼容
        return properties;
    }

    /**
     * Determine if JCache is available. This either kicks in if a provider is available
     * as defined per {@link JCacheProviderAvailableCondition} or if a
     * {@link CacheManager} has already been defined.
     */
    @Order(Ordered.LOWEST_PRECEDENCE)
    static class JCacheAvailableCondition extends AnyNestedCondition {

        JCacheAvailableCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @Conditional(JCacheProviderAvailableCondition.class)
        static class JCacheProvider {

        }

        @ConditionalOnSingleCandidate(CacheManager.class)
        static class CustomJCacheCacheManager {

        }

    }

    /**
     * Determine if a JCache provider is available. This either kicks in if a default
     * {@link CachingProvider} has been found or if the property referring to the provider
     * to use has been set.
     */
    @Order(Ordered.LOWEST_PRECEDENCE)
    static class JCacheProviderAvailableCondition extends SpringBootCondition {

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConditionMessage.Builder message = ConditionMessage.forCondition("JCache");
            String providerProperty = "spring.cache.jcache.provider";
            if (context.getEnvironment().containsProperty(providerProperty)) {
                return ConditionOutcome.match(message.because("JCache provider specified"));
            }
            Iterator<CachingProvider> providers = Caching.getCachingProviders().iterator();
            if (!providers.hasNext()) {
                return ConditionOutcome.noMatch(message.didNotFind("JSR-107 provider").atAll());
            }
            providers.next();
            if (providers.hasNext()) {
                return ConditionOutcome.noMatch(message.foundExactly("multiple JSR-107 providers"));
            }
            return ConditionOutcome.match(message.foundExactly("single JSR-107 provider"));
        }

    }

}
