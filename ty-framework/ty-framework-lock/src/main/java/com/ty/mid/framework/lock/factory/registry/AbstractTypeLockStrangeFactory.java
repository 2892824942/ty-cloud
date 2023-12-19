package com.ty.mid.framework.lock.factory.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import org.springframework.integration.support.locks.LockRegistry;

public abstract class AbstractTypeLockStrangeFactory {
    protected LockConfig lockConfig;

    public AbstractTypeLockStrangeFactory(LockConfig lockConfig) {
        this.lockConfig = lockConfig;
    }

    public abstract LockFactory getLockFactory();

    public abstract LockConfig.LockImplementer implementerType();

    public abstract boolean supportTransaction();

    public abstract boolean withLocalCache();

    public abstract LockRegistry getLockRegistry();
}
