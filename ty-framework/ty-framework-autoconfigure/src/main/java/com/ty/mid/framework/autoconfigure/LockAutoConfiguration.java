package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.config.spi.LockSpiClassLoader;
import com.ty.mid.framework.lock.core.BusinessKeyProvider;
import com.ty.mid.framework.lock.core.LockAspect;
import com.ty.mid.framework.lock.core.LockInfoProvider;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.registry.AbstractTypeLockStrangeFactory;
import com.ty.mid.framework.lock.factory.registry.LockRegistryFactory;
import com.ty.mid.framework.lock.factory.support.TypeLockRegistryFactory;
import com.ty.mid.framework.lock.factory.support.registry.*;
import com.ty.mid.framework.lock.handler.LockHandler;
import com.ty.mid.framework.lock.handler.lock.ExceptionOnLockCustomerHandler;
import com.ty.mid.framework.lock.handler.lock.FailOnLockCustomerHandler;
import com.ty.mid.framework.lock.handler.release.ReleaseTimeoutCustomerHandler;
import com.ty.mid.framework.lock.model.ExceptionOnLockStrategy;
import com.ty.mid.framework.lock.model.FailOnLockStrategy;
import com.ty.mid.framework.lock.model.ReleaseTimeoutStrategy;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2022/03/26
 * Content :lock自动装配
 */
@ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "enable", havingValue = "true")
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(LockConfig.class)
@ConditionalOnBean(RedissonClient.class)
public class LockAutoConfiguration {

    @Bean
    public LockInfoProvider lockInfoProvider() {
        return new LockInfoProvider();
    }

    @Bean
    public BusinessKeyProvider businessKeyProvider() {
        return new BusinessKeyProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeLockRegistryFactory typeLockRegistryFactory(LockConfig lockConfig, RedissonClient redissonClient) {
        List<AbstractTypeLockStrangeFactory> typeRegistryFactories = new ArrayList<>();
        typeRegistryFactories.add(new LocalDefaultTypeLockFactory(lockConfig));
        typeRegistryFactories.add(new LocalTransactionSupportTypeLockFactory(lockConfig));
        typeRegistryFactories.add(new RedisDefaultTypeLockFactory(redissonClient, lockConfig));
        typeRegistryFactories.add(new RedisTransactionSupportTypeLockFactory(redissonClient, lockConfig));
        typeRegistryFactories.add(new RedisTransactionSupportWithLocalCacheTypeLockFactory(redissonClient, lockConfig));
        typeRegistryFactories.add(new RedisWithLocalCacheTypeLockFactory(redissonClient, lockConfig));
        return new TypeLockRegistryFactory(typeRegistryFactories);
    }

    @Bean
    public LockRegistry lockRegistry(LockConfig lockConfig, TypeLockRegistryFactory typeLockRegistryFactory) {
        loadStrategy(lockConfig);
        return typeLockRegistryFactory.getLockRegistry(lockConfig.getImplementer(), lockConfig.isSupportTransaction(), lockConfig.isWithLocalCache());
    }

    /**
     * lock切面
     *
     * @param lockRegistryFactory lockRegistryFactory
     * @param lockFactory         lockFactory
     * @param lockInfoProvider    lockInfoProvider
     * @return LockAspect
     */
    @Bean
    @ConditionalOnMissingBean
    LockAspect lockAspect(LockRegistryFactory lockRegistryFactory, LockFactory lockFactory, LockInfoProvider lockInfoProvider) {
        return new LockAspect(lockRegistryFactory, lockFactory, lockInfoProvider);
    }


    @Bean
    public LockFactory lockFactory(LockConfig lockConfig, TypeLockRegistryFactory typeLockRegistryFactory) {
        return typeLockRegistryFactory.getLockFactory(lockConfig.getImplementer(), lockConfig.isSupportTransaction(), lockConfig.isWithLocalCache());
    }

    private void loadStrategy(LockConfig lockConfig) {
        doLoadStrategy(FailOnLockCustomerHandler.class, FailOnLockStrategy.CUSTOMER == lockConfig.getLockFailStrategy());
        doLoadStrategy(ExceptionOnLockCustomerHandler.class, ExceptionOnLockStrategy.CUSTOMER == lockConfig.getExceptionOnLockStrategy());
        doLoadStrategy(ReleaseTimeoutCustomerHandler.class, ReleaseTimeoutStrategy.CUSTOMER == lockConfig.getReleaseTimeoutStrategy());
    }

    private void doLoadStrategy(Class<? extends LockHandler> lockHandlerClass, boolean validate) {
        List<? extends LockHandler> lockHandlers = LockSpiClassLoader.getInstance().loaderClass(lockHandlerClass);
        if (validate) {
            Validator.requireNonEmpty(lockHandlers, "No customer " + lockHandlerClass.getName() + " find in your application resource dir: META-INFO/services");
        }
    }


}
