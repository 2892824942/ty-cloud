package com.ty.mid.framework.lock.manager.support;

import com.ty.mid.framework.lock.enums.LockImplementer;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.support.ZkLockFactory;
import com.ty.mid.framework.lock.manager.AbstractTypeLockManager;
import com.ty.mid.framework.lock.registry.support.ZkLockRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.integration.support.locks.LockRegistry;

public class ZkLockManager extends AbstractTypeLockManager {
    CuratorFramework curatorFramework;
    LockFactory lockFactory;

    public ZkLockManager(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
        this.lockFactory = new ZkLockFactory(curatorFramework);
    }

    @Override
    public LockFactory getLockFactory() {
        return lockFactory;
    }

    @Override
    public LockImplementer implementerType() {
        return LockImplementer.ZOOKEEPER;
    }


    @Override
    public LockRegistry getLockRegistry() {
        return new ZkLockRegistry(lockFactory);
    }
}
