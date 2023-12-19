package com.ty.mid.framework.lock.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import org.springframework.util.Assert;

import java.util.concurrent.locks.Lock;

public abstract class AbstractLockRegistry implements TypeLockRegistry {
    private LockConfig lockConfig;

    private LockFactory lockFactory;

    public AbstractLockRegistry(LockConfig lockConfig, LockFactory lockFactory) {
        this.lockConfig = lockConfig;
        this.lockFactory = lockFactory;

    }

    public LockConfig getLockConfig() {
        return lockConfig;
    }

    public LockFactory getLockFactory() {
        return lockFactory;
    }

    protected Lock getNewLock(String lockType, String lockKey) {
        return getLockFactory().getLock(lockType, lockKey);
    }

    @Override
    public Lock obtain(Object lockKey) {
        return this.obtain(null, lockKey);
    }

    @Override
    public Lock obtain(String type, Object lockKey) {
        Assert.isInstanceOf(String.class, lockKey);
        String path = (String) lockKey;
        return getNewLock(type, path);
    }
}
