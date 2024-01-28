package com.ty.mid.framework.lock.factory.support;

import com.ty.mid.framework.core.cache.Cache;
import com.ty.mid.framework.core.cache.support.InMemoryCache;
import com.ty.mid.framework.lock.enums.LockScopeType;
import com.ty.mid.framework.lock.enums.LockType;
import com.ty.mid.framework.lock.factory.LockFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalLockFactory implements LockFactory {
    /**
     * 自带过期时间，重置后续期
     */
    private final Cache<String, ReadWriteLock> readWriteLockCache = new InMemoryCache<>(false);
    protected TimeUnit timeUnit = TimeUnit.SECONDS;
    /**
     * 10分钟足够业务运行
     */
    protected long expiration = 60*10;
    public LocalLockFactory() {
    }

    @Override
    public Lock getLock(String type, String lockKey) {
        // 获取锁类型
        LockType lockTypeEnum = LockType.of(type);
        if (Objects.isNull(lockTypeEnum)) {
            return new ReentrantLock();
        }
        switch (lockTypeEnum) {
            case Fair:
                return new ReentrantLock(true);
            case Read:
                return readWriteLockCache.computeIfAbsent(lockKey, new ReentrantReadWriteLock(), timeUnit, expiration).readLock();
            case Write:
                return readWriteLockCache.computeIfAbsent(lockKey, new ReentrantReadWriteLock(), timeUnit, expiration).writeLock();
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
