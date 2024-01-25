package com.ty.mid.framework.lock.manager.support;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.manager.AbstractTypeLockManager;
import com.ty.mid.framework.lock.factory.support.LocalLockFactory;
import com.ty.mid.framework.lock.registry.support.local.JvmLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

public class EmptyLockManager extends AbstractTypeLockManager {

    public EmptyLockManager() {

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
    public LockRegistry getLockRegistry() {
        return new JvmLockRegistry(getLockFactory());
    }
}
