package com.ty.mid.framework.lock.factory.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import org.springframework.integration.support.locks.LockRegistry;

public abstract class AbstractTypeLockManager {

    public abstract LockFactory getLockFactory();

    public abstract LockConfig.LockImplementer implementerType();

    public abstract LockRegistry getLockRegistry();
}
