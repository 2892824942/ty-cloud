package com.ty.mid.framework.lock.manager.support;

import com.ty.mid.framework.lock.enums.LockImplementer;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.support.JvmLockFactory;
import com.ty.mid.framework.lock.manager.AbstractTypeLockManager;
import com.ty.mid.framework.lock.registry.support.JvmLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

public class JvmLockManager extends AbstractTypeLockManager {


    @Override
    public LockFactory getLockFactory() {
        return JvmLockFactory.getInstance();
    }

    @Override
    public LockImplementer implementerType() {
        return LockImplementer.JVM;
    }

    @Override
    public LockRegistry getLockRegistry() {
        return new JvmLockRegistry(getLockFactory());
    }
}
