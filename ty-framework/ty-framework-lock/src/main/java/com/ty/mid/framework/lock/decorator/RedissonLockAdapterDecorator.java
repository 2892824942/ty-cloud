package com.ty.mid.framework.lock.decorator;

import com.ty.mid.framework.lock.adapter.LockAdapter;
import com.ty.mid.framework.lock.core.LockInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
public class RedissonLockAdapterDecorator extends AbstractLockDecorator {
    protected LockAdapter lockAdapter;

    public RedissonLockAdapterDecorator(Lock distributedLock, LockInfo lockInfo, LockAdapter lockAdapter) {
        super(distributedLock, lockInfo);
        this.lockAdapter = lockAdapter;
    }

    @Override
    public void lock() {
        lockAdapter.acquireNoWaitTime(realLock, lockInfo);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            lockAdapter.acquireInterruptibly(realLock, lockInfo);
        } catch (InterruptedException interruptedException) {
            this.realLock.unlock();
            Thread.currentThread().interrupt();
            throw interruptedException;
        } catch (Exception e) {
            this.realLock.unlock();
            this.rethrowAsLockException(e);
        }
    }

    @Override
    public boolean tryLock() {
        return lockAdapter.acquireNoTime(realLock, lockInfo);
    }

    /**
     * 对于包装类来说，可等待的时间要尽量放在本地缓存中，而不是和分布式锁交互
     *
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lockAdapter.acquire(realLock, lockInfo);
    }


    @Override
    public void unlock() {
        lockAdapter.release(realLock, lockInfo);
    }

}