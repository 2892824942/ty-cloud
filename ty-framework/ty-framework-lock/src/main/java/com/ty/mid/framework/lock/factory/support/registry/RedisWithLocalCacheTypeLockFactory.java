package com.ty.mid.framework.lock.factory.support.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.registry.AbstractTypeLockStrangeFactory;
import com.ty.mid.framework.lock.factory.support.RedissonDefaultLockFactory;
import com.ty.mid.framework.lock.support.redis.redisson.RedissonWithLockCacheLockRegistry;
import org.redisson.api.RedissonClient;
import org.springframework.integration.support.locks.LockRegistry;

public class RedisWithLocalCacheTypeLockFactory extends AbstractTypeLockStrangeFactory {
    RedissonClient redissonClient;
    LockFactory lockFactory;

    public RedisWithLocalCacheTypeLockFactory(RedissonClient redissonClient, LockConfig lockConfig) {
        super(lockConfig);
        this.redissonClient = redissonClient;
        this.lockFactory = new RedissonDefaultLockFactory(redissonClient);
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
    public boolean supportTransaction() {
        return true;
    }

    @Override
    public boolean withLocalCache() {
        return true;
    }

    @Override
    public LockRegistry getLockRegistry() {
        return new RedissonWithLockCacheLockRegistry(super.lockConfig, lockFactory);
    }
}
