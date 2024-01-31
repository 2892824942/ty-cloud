package com.ty.mid.framework.lock.manager;

import com.ty.mid.framework.lock.enums.LockImplementer;
import com.ty.mid.framework.lock.factory.LockFactory;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.List;

public interface LockManagerKeeper {
    LockRegistry getLockRegistry(LockImplementer lockImplementer);

    LockFactory getLockFactory(LockImplementer lockImplementer);

    List<AbstractTypeLockManager> getTypeLockManagers();

}