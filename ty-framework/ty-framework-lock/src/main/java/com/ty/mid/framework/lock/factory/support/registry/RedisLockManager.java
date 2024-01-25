package com.ty.mid.framework.lock.factory.support.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.registry.AbstractTypeLockManager;
import com.ty.mid.framework.lock.factory.support.RedissonLockFactory;
import com.ty.mid.framework.lock.registry.support.redis.redisson.RedissonLockRegistry;
import org.redisson.api.RedissonClient;
import org.springframework.integration.support.locks.LockRegistry;

public class RedisLockManager extends AbstractTypeLockManager {
    RedissonClient redissonClient;
    LockFactory lockFactory;

    public RedisLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.lockFactory = new RedissonLockFactory(redissonClient);
    }

    @Override
    public LockFactory getLockFactory() {
        return lockFactory;
    }

    @Override
    public LockConfig.LockImplementer implementerType() {
        return LockConfig.LockImplementer.REDIS;
    }


    @Override
    public LockRegistry getLockRegistry() {
        return new RedissonLockRegistry(lockFactory);
    }
}
