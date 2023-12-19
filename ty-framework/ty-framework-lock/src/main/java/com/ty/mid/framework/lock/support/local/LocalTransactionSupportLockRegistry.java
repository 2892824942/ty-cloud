package com.ty.mid.framework.lock.support.local;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.support.LocalLockFactory;
import com.ty.mid.framework.lock.registry.AbstractTransactionSupportLockRegistry;

/**
 * 单例的本地lock注册
 */
public class LocalTransactionSupportLockRegistry extends AbstractTransactionSupportLockRegistry {
    private static LockFactory lockFactory = new LocalLockFactory();
    private static LocalTransactionSupportLockRegistry localLockRegistry = new LocalTransactionSupportLockRegistry(null, lockFactory);


    public LocalTransactionSupportLockRegistry(LockConfig lockConfig, LockFactory lockFactory) {
        super(lockConfig, lockFactory);
    }

    public static LocalTransactionSupportLockRegistry getInstance() {
        return localLockRegistry;
    }
}
