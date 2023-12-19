package com.ty.mid.framework.lock.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.decorator.LocalCacheLockDecorator;
import com.ty.mid.framework.lock.factory.LockFactory;

import java.util.concurrent.locks.Lock;

/**
 * 带有本地缓存的lock锁，锁的操作先本地执行，本地执行成功后才会真正使用distributedLock执行
 */
public abstract class AbstractLocalCacheDistributedLockRegistry extends AbstractCacheLockRegistry {


    public AbstractLocalCacheDistributedLockRegistry(LockConfig lockConfig, LockFactory lockFactory) {
        super(lockConfig, lockFactory);
    }

    @Override
    protected Lock getNewLock(String lockType, String lockKey) {
        return new LocalCacheLockDecorator(lockKey, super.getNewLock(lockType, lockKey), getLockConfig());
    }
}
