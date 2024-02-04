package com.ty.mid.framework.lock.registry.support;

import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.registry.AbstractDecorateLockRegistry;

public class RedissonLockRegistry extends AbstractDecorateLockRegistry {


    public RedissonLockRegistry(LockFactory redissonLockFactory) {
        super(redissonLockFactory);

    }
}
