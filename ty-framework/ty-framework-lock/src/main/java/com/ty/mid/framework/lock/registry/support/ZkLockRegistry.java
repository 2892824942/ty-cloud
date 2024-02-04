package com.ty.mid.framework.lock.registry.support;

import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.registry.AbstractDecorateLockRegistry;

public class ZkLockRegistry extends AbstractDecorateLockRegistry {


    public ZkLockRegistry(LockFactory zkLockFactory) {
        super(zkLockFactory);

    }
}
