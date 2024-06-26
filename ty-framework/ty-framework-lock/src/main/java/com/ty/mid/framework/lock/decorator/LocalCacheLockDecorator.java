package com.ty.mid.framework.lock.decorator;

import com.ty.mid.framework.lock.core.LockInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class LocalCacheLockDecorator extends AbstractLockDecorator {
    protected ReentrantLock localLock = new ReentrantLock();

    public LocalCacheLockDecorator(Lock distributedLock, LockInfo lockInfo) {
        super(distributedLock, lockInfo);
    }

    @Override
    public void lock() {
        this.localLock.lock();
        realLock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            this.localLock.lockInterruptibly();
            this.realLock.lockInterruptibly();
        } catch (InterruptedException interruptedException) {
            this.localLock.unlock();
            this.realLock.unlock();
            Thread.currentThread().interrupt();
            throw interruptedException;
        } catch (Exception e) {
            this.localLock.unlock();
            this.realLock.unlock();
            this.rethrowAsLockException(e);
        }
    }

    @Override
    public boolean tryLock() {
        try {
            return tryLock(0, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
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
        long startTime = System.currentTimeMillis();
        if (!this.localLock.tryLock(time, unit)) {
            return false;
        }
        long during = System.currentTimeMillis() - startTime;
        try {
            //等待时间转接
            boolean acquired = realLock.tryLock(Math.max(unit.toMillis(time) - during, 0L), TimeUnit.MILLISECONDS);
            if (!acquired) {
                this.localLock.unlock();
            }
            return acquired;
        } catch (Exception e) {
            this.localLock.unlock();
            rethrowAsLockException(e);
        }
        return false;
    }


    @Override
    public void unlock() {
        if (!this.localLock.isHeldByCurrentThread()) {
            //本地锁未持有,直接忽略
            return;
        }
        if (this.localLock.getHoldCount() >= 1) {
            this.localLock.unlock();
            realLock.unlock();
        }
    }

}