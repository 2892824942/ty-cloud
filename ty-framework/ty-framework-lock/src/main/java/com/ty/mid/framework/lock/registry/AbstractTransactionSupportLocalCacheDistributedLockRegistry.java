package com.ty.mid.framework.lock.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.decorator.LocalCacheLockDecorator;
import com.ty.mid.framework.lock.decorator.TransactionLockDecorator;
import com.ty.mid.framework.lock.factory.LockFactory;

import java.util.concurrent.locks.Lock;

/**
 * 带有本地缓存的且支持事务上下文的lock锁，锁的操作先本地执行，本地执行成功后才会真正使用distributedLock执行
 */
public abstract class AbstractTransactionSupportLocalCacheDistributedLockRegistry extends AbstractCacheLockRegistry {


    public AbstractTransactionSupportLocalCacheDistributedLockRegistry(LockConfig lockConfig, LockFactory lockFactory) {
        super(lockConfig, lockFactory);
    }

    @Override
    protected Lock getNewLock(String lockType, String lockKey) {
        return new TransactionLockDecorator(lockKey, new LocalCacheLockDecorator(lockKey, super.getNewLock(lockType, lockKey), getLockConfig()), getLockConfig());
    }
}
