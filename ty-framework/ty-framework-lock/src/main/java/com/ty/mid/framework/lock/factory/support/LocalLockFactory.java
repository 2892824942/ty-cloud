package com.ty.mid.framework.lock.factory.support;

import com.ty.mid.framework.lock.enums.LockScopeType;
import com.ty.mid.framework.lock.enums.LockType;
import com.ty.mid.framework.lock.factory.LockFactory;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalLockFactory implements LockFactory {

    public LocalLockFactory() {
    }

    @Override
    public Lock getLock(String type, String lockKey) {

        LockType lockTypeEnum = LockType.of(type);
        if (Objects.isNull(lockTypeEnum)) {
            return new ReentrantLock();
        }
        switch (lockTypeEnum) {
            case Fair:
                return new ReentrantLock(true);
            case Read:
                return new ReentrantReadWriteLock().readLock();
            case Write:
                return new ReentrantReadWriteLock().writeLock();
            case Reentrant:
            default:
                return new ReentrantLock();
        }
    }

    @Override
    public LockScopeType getScopeType() {
        return LockScopeType.Local;
    }

}
