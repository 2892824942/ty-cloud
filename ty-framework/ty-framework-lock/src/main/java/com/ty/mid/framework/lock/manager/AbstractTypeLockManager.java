package com.ty.mid.framework.lock.manager;

import com.ty.mid.framework.lock.enums.LockImplementer;
import com.ty.mid.framework.lock.factory.LockFactory;
import org.springframework.integration.support.locks.LockRegistry;

public abstract class AbstractTypeLockManager {

    public abstract LockFactory getLockFactory();

    public abstract LockImplementer implementerType();

    public abstract LockRegistry getLockRegistry();
}
