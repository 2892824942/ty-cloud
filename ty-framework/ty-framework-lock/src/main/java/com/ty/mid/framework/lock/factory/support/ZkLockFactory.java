package com.ty.mid.framework.lock.factory.support;

import com.ty.mid.framework.lock.enums.LockScopeType;
import com.ty.mid.framework.lock.enums.LockType;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.lock.ZkLock;
import com.ty.mid.framework.lock.lock.ZkReadWritLock;
import lombok.Getter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

import java.util.Objects;
import java.util.concurrent.locks.Lock;

@Getter
public class ZkLockFactory implements LockFactory {
    private static final String CHARACTER = "/";
    protected CuratorFramework curatorFramework;

    public ZkLockFactory(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    @Override
    public Lock getLock(String type, String lockKey) {

        LockType lockTypeEnum = LockType.of(type);
        if (Objects.isNull(lockTypeEnum)) {
            return new ZkLock(new InterProcessMutex(curatorFramework, CHARACTER.concat(lockKey)));
        }
        switch (lockTypeEnum) {
            case Read:
                return new ZkReadWritLock(new InterProcessReadWriteLock(curatorFramework, lockKey)).readLock();
            case Write:
                return new ZkReadWritLock(new InterProcessReadWriteLock(curatorFramework, lockKey)).writeLock();
            case Reentrant:
            case Fair:
            default:
                return new ZkLock(new InterProcessMutex(curatorFramework, lockKey));
        }
    }

    @Override
    public LockScopeType getScopeType() {
        return LockScopeType.Distributed;
    }

}
