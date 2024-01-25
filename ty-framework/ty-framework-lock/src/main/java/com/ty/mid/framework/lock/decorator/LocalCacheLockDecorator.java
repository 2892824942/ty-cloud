package com.ty.mid.framework.lock.decorator;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.core.LockInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;

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
        distributedLock.lock();
    }

    private void rethrowAsLockException(Exception e) {
        throw new CannotAcquireLockException("Failed to lock mutex at " + this.lockInfo.getName(), e);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.localLock.lockInterruptibly();
        this.distributedLock.lockInterruptibly();
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
            boolean acquired = distributedLock.tryLock(Math.max(unit.toMillis(time) - during, 0L), TimeUnit.MILLISECONDS);
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
            throw new IllegalStateException("You do not own lock at " + this.lockInfo.getName());
        }
        if (this.localLock.getHoldCount() >= 1) {
            this.localLock.unlock();
            distributedLock.unlock();
        }
    }

}