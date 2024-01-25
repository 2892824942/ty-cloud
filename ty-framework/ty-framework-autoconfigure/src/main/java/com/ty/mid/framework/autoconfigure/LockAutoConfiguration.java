package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.config.spi.LockSpiClassLoader;
import com.ty.mid.framework.lock.core.BusinessKeyProvider;
import com.ty.mid.framework.lock.core.LockAspect;
import com.ty.mid.framework.lock.core.LockInfoProvider;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.registry.AbstractTypeLockManager;
import com.ty.mid.framework.lock.factory.registry.LockManagerKeeper;
import com.ty.mid.framework.lock.factory.support.TypeLockManagerKeeper;
import com.ty.mid.framework.lock.factory.support.registry.*;
import com.ty.mid.framework.lock.handler.LockHandler;
import com.ty.mid.framework.lock.handler.lock.ExceptionOnLockCustomerHandler;
import com.ty.mid.framework.lock.handler.lock.FailOnLockCustomerHandler;
import com.ty.mid.framework.lock.handler.release.ReleaseTimeoutCustomerHandler;
import com.ty.mid.framework.lock.strategy.ExceptionOnLockStrategy;
import com.ty.mid.framework.lock.strategy.FailOnLockStrategy;
import com.ty.mid.framework.lock.strategy.ReleaseTimeoutStrategy;
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
    public LockManagerKeeper typeLockRegistryFactory(RedissonClient redissonClient) {
        List<AbstractTypeLockManager> typeLockManagers = new ArrayList<>();
        typeLockManagers.add(new JvmLockManager());
        typeLockManagers.add(new RedisLockManager(redissonClient));
        return new TypeLockManagerKeeper(typeLockManagers);
    }

    @Bean
    public LockRegistry lockRegistry(LockConfig lockConfig, LockManagerKeeper LockManagerKeeper) {
        loadStrategy(lockConfig);
        return LockManagerKeeper.getLockRegistry(lockConfig.getImplementer());
    }

    /**
     * lock切面
     *
     * @return LockAspect
     */
    @Bean
    @ConditionalOnMissingBean
    LockAspect lockAspect() {
        return new LockAspect();
    }


    @Bean
    public LockFactory lockFactory(LockConfig lockConfig, TypeLockManagerKeeper typeLockRegistryFactory) {
        return typeLockRegistryFactory.getLockFactory(lockConfig.getImplementer());
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
