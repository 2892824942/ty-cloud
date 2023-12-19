package com.ty.mid.framework.lock.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.decorator.TransactionLockDecorator;
import com.ty.mid.framework.lock.factory.LockFactory;

import java.util.concurrent.locks.Lock;

/**
 * 可感知事物上下文的锁，可在事务上下文中做一些操作，比如事务提交后才可释放锁
 */
public abstract class AbstractTransactionSupportLockRegistry extends AbstractLockRegistry implements TypeLockRegistry {


    public AbstractTransactionSupportLockRegistry(LockConfig lockConfig, LockFactory lockFactory) {
        super(lockConfig, lockFactory);
    }

    @Override
    protected Lock getNewLock(String lockType, String lockKey) {
        return new TransactionLockDecorator(lockKey, super.getNewLock(lockType, lockKey), getLockConfig());
    }


}
