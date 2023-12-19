package com.ty.mid.framework.lock.factory.support.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.registry.AbstractTypeLockStrangeFactory;
import com.ty.mid.framework.lock.factory.support.LocalLockFactory;
import com.ty.mid.framework.lock.support.local.LocalTransactionSupportLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

public class LocalTransactionSupportTypeLockFactory extends AbstractTypeLockStrangeFactory {
    LockFactory lockFactory = new LocalLockFactory();

    public LocalTransactionSupportTypeLockFactory(LockConfig lockConfig) {
        super(lockConfig);
    }

    @Override
    public LockFactory getLockFactory() {
        return lockFactory;
    }

    @Override
    public LockConfig.LockImplementer implementerType() {
        return LockConfig.LockImplementer.JVM;
    }

    @Override
    public boolean supportTransaction() {
        return true;
    }

    @Override
    public boolean withLocalCache() {
        return false;
    }

    @Override
    public LockRegistry getLockRegistry() {
        return LocalTransactionSupportLockRegistry.getInstance();
    }
}
