package com.ty.mid.framework.lock.factory.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import org.springframework.integration.support.locks.LockRegistry;

public interface LockRegistryFactory {
    LockRegistry getLockRegistry(LockConfig.LockImplementer lockImplementer, boolean supportTransaction, boolean withLocalCache);

    LockFactory getLockFactory(LockConfig.LockImplementer lockImplementer, boolean supportTransaction, boolean withLocalCache);

}