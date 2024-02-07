package com.ty.mid.framework.lock.manager.support;

import com.ty.mid.framework.lock.enums.LockImplementer;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.support.RedissonLockFactory;
import com.ty.mid.framework.lock.manager.AbstractTypeLockManager;
import com.ty.mid.framework.lock.registry.support.RedissonLockRegistry;
import org.redisson.api.RedissonClient;
import org.springframework.integration.support.locks.LockRegistry;

public class RedisLockManager extends AbstractTypeLockManager {
    RedissonClient redissonClient;
    LockFactory lockFactory;

    public RedisLockManager(RedissonClient redissonClient,RedissonLockFactory redissonLockFactory) {
        this.redissonClient = redissonClient;
        this.lockFactory = redissonLockFactory;
    }

    @Override
    public LockFactory getLockFactory() {
        return lockFactory;
    }

    @Override
    public LockImplementer implementerType() {
        return LockImplementer.REDIS;
    }


    @Override
    public LockRegistry getLockRegistry() {
        return new RedissonLockRegistry(lockFactory);
    }
}
