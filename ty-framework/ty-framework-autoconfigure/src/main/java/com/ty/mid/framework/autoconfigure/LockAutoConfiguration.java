package com.ty.mid.framework.autoconfigure;

import cn.hutool.core.lang.Assert;
import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.config.spi.LockSpiClassLoader;
import com.ty.mid.framework.lock.core.BusinessKeyProvider;
import com.ty.mid.framework.lock.core.LockAspect;
import com.ty.mid.framework.lock.core.LockInfoProvider;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.handler.LockHandler;
import com.ty.mid.framework.lock.handler.lock.ExceptionOnLockCustomerHandler;
import com.ty.mid.framework.lock.handler.lock.FailOnLockCustomerHandler;
import com.ty.mid.framework.lock.handler.release.ReleaseExceptionCustomerHandler;
import com.ty.mid.framework.lock.manager.AbstractTypeLockManager;
import com.ty.mid.framework.lock.manager.LockManagerKeeper;
import com.ty.mid.framework.lock.manager.TypeLockManagerKeeper;
import com.ty.mid.framework.lock.manager.support.JvmLockManager;
import com.ty.mid.framework.lock.strategy.ExceptionOnLockStrategy;
import com.ty.mid.framework.lock.strategy.FailOnLockStrategy;
import com.ty.mid.framework.lock.strategy.ReleaseExceptionStrategy;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2022/03/26 <p>
 * Content :lock自动装配 <p>
 * 只要加入自动配置,直接会加载,除非enable设置为false
 */
@ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "enable", matchIfMissing = true)
@EnableConfigurationProperties(LockConfig.class)
@AutoConfigureAfter({RedisAutoConfiguration.class, LockZookeeperAutoConfiguration.class})
public class LockAutoConfiguration {

    @Bean
    public LockInfoProvider lockInfoProvider() {
        return new LockInfoProvider();
    }

    @Bean
    public BusinessKeyProvider businessKeyProvider(LockConfig lockConfig) {
        loadStrategy(lockConfig);
        return new BusinessKeyProvider(lockConfig);
    }

    @Bean
    public LockManagerKeeper lockManagerKeeper() {
        List<AbstractTypeLockManager> typeLockManagers = new ArrayList<>();
        typeLockManagers.add(new JvmLockManager());
        return new TypeLockManagerKeeper(typeLockManagers);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "implementer", havingValue = "jvm")
    public LockRegistry lockRegistry(LockManagerKeeper lockManagerKeeper, LockConfig lockConfig) {
        return lockManagerKeeper.getLockRegistry(lockConfig.getImplementer());
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "implementer", havingValue = "jvm")
    public LockFactory lockFactory(LockManagerKeeper lockManagerKeeper, LockConfig lockConfig) {
        return lockManagerKeeper.getLockFactory(lockConfig.getImplementer());
    }

    /**
     * lock切面
     *
     * @return LockAspect
     */
    @Bean
    @ConditionalOnMissingBean
    LockAspect lockAspect(LockManagerKeeper lockManagerKeeper, LockInfoProvider lockInfoProvider) {
        return new LockAspect(lockManagerKeeper, lockInfoProvider);
    }

    private void loadStrategy(LockConfig lockConfig) {
        doLoadStrategy(FailOnLockCustomerHandler.class, FailOnLockStrategy.CUSTOMER == lockConfig.getLockFailStrategy());
        doLoadStrategy(ExceptionOnLockCustomerHandler.class, ExceptionOnLockStrategy.CUSTOMER == lockConfig.getExceptionOnLockStrategy());
        doLoadStrategy(ReleaseExceptionCustomerHandler.class, ReleaseExceptionStrategy.CUSTOMER == lockConfig.getReleaseExceptionStrategy());
    }

    private void doLoadStrategy(Class<? extends LockHandler> lockHandlerClass, boolean validate) {
        List<? extends LockHandler> lockHandlers = LockSpiClassLoader.getInstance().loaderClass(lockHandlerClass);
        if (validate) {
            Assert.notEmpty(lockHandlers, "No customer " + lockHandlerClass.getName() + " find in your application resource dir: META-INFO/services");
        }
    }


}
