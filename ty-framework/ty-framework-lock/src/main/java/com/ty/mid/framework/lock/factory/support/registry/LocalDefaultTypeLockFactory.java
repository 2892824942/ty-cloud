package com.ty.mid.framework.lock.factory.support.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.registry.AbstractTypeLockStrangeFactory;
import com.ty.mid.framework.lock.factory.support.LocalLockFactory;
import com.ty.mid.framework.lock.support.local.LocalLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

public class LocalDefaultTypeLockFactory extends AbstractTypeLockStrangeFactory {

    public LocalDefaultTypeLockFactory(LockConfig lockConfig) {
        super(lockConfig);
    }

    @Override
    public LockFactory getLockFactory() {
        return new LocalLockFactory();
    }

    @Override
    public LockConfig.LockImplementer implementerType() {
        return LockConfig.LockImplementer.JVM;
    }

    @Override
    public boolean supportTransaction() {
        return false;
    }

    @Override
    public boolean withLocalCache() {
        return false;
    }

    @Override
    public LockRegistry getLockRegistry() {
        return new LocalLockRegistry(super.lockConfig, getLockFactory());
    }
}
