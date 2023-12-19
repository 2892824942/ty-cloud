package com.ty.mid.framework.lock.support.redis.redisson;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.registry.AbstractTransactionSupportLockRegistry;

public class RedissonTransactionSupportLockRegistry extends AbstractTransactionSupportLockRegistry {
    public RedissonTransactionSupportLockRegistry(LockConfig lockConfig, LockFactory redissonLockFactory) {
        super(lockConfig, redissonLockFactory);
    }
}
