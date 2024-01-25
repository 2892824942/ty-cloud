package com.ty.mid.framework.lock.factory.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import org.springframework.integration.support.locks.LockRegistry;

public interface LockManagerKeeper {
    LockRegistry getLockRegistry(LockConfig.LockImplementer lockImplementer);

    LockFactory getLockFactory(LockConfig.LockImplementer lockImplementer);

}