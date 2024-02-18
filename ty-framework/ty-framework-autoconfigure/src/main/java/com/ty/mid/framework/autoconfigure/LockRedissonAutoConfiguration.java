package com.ty.mid.framework.autoconfigure;

import cn.hutool.core.util.BooleanUtil;
import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.support.RedissonLockAdapterFactory;
import com.ty.mid.framework.lock.factory.support.RedissonLockFactory;
import com.ty.mid.framework.lock.manager.AbstractTypeLockManager;
import com.ty.mid.framework.lock.manager.LockManagerKeeper;
import com.ty.mid.framework.lock.manager.support.JvmLockManager;
import com.ty.mid.framework.lock.manager.support.RedisLockManager;
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
 * 满足条件,redis分布式锁就会自动加载到Spring上下文已供使用
 */
@ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "enable", matchIfMissing = true)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(LockConfig.class)
@ConditionalOnBean(RedissonClient.class)
public class LockRedissonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedisLockManager redisLockManager(LockManagerKeeper LockManagerKeeper, RedissonClient redissonClient, LockConfig lockConfig) {
        List<AbstractTypeLockManager> typeLockManagers = LockManagerKeeper.getTypeLockManagers();
        if (typeLockManagers == null) {
            typeLockManagers = new ArrayList<>();
            typeLockManagers.add(new JvmLockManager());
        }
        //是否开启方言支持
        RedissonLockFactory redissonLockFactory = BooleanUtil.isTrue(lockConfig.getDialect())
                ? new RedissonLockAdapterFactory(redissonClient) : new RedissonLockFactory(redissonClient);
        RedisLockManager redisLockManager = new RedisLockManager(redissonClient, redissonLockFactory);
        typeLockManagers.add(redisLockManager);
        return redisLockManager;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "implementer", havingValue = "redis")
    public LockRegistry lockRegistry(RedisLockManager redisLockManager) {
        return redisLockManager.getLockRegistry();
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "implementer", havingValue = "redis")
    public LockFactory lockFactory(RedisLockManager redisLockManager) {
        return redisLockManager.getLockFactory();
    }


}
