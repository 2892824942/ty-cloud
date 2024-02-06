package com.ty.mid.framework.lock.adapter.support;

import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.lock.adapter.LockAdapter;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.decorator.AbstractLockDecorator;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;

@Slf4j
public class RedissonLockAdapter implements LockAdapter {
    @Override
    public boolean acquire(Lock lock, LockInfo lockInfo) {
        try {
            RLock rLock = this.convert2RLock(lock);
            return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), lockInfo.getTimeUnit());
        } catch (InterruptedException e) {
            log.warn("release lock have InterruptedException e:", e);
            throw new FrameworkException(e);
        }
    }


    @Override
    public boolean acquireNoTime(Lock lock, LockInfo lockInfo) {
        RLock rLock = this.convert2RLock(lock);
        return rLock.tryLock();

    }

    @Override
    public boolean acquireNoWaitTime(Lock lock, LockInfo lockInfo) {
        try {
            RLock rLock = this.convert2RLock(lock);
            return rLock.tryLock(lockInfo.getLeaseTime(), lockInfo.getTimeUnit());
        } catch (InterruptedException e) {
            log.warn("release lock have InterruptedException e:", e);
            throw new FrameworkException(e);
        }
    }

    @Override
    public boolean acquireInterruptibly(Lock lock, LockInfo lockInfo) {
        try {
            RLock rLock = this.convert2RLock(lock);
            rLock.lockInterruptibly(lockInfo.getLeaseTime(), lockInfo.getTimeUnit());
        } catch (InterruptedException e) {
            log.warn("release lock have InterruptedException e:", e);
            throw new FrameworkException(e);
        }
        return true;
    }


    @Override
    public boolean release(Lock lock, LockInfo lockInfo) {
        RLock rLock = this.convert2RLock(lock);
        if (rLock.isHeldByCurrentThread()) {
            try {
                return rLock.forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                log.warn("release lock have InterruptedException | ExecutionException e:", e);
                throw new FrameworkException(e);
            }
        }
        return false;
    }

    private RLock convert2RLock(Lock lock) {
        Lock realLock = lock;
        if (realLock instanceof AbstractLockDecorator) {
            do {
                //循环拆掉装饰者,找到最底层的Lock
                AbstractLockDecorator abstractLockDecorator = (AbstractLockDecorator) lock;
                realLock = abstractLockDecorator.getRealLock();
            } while (realLock instanceof AbstractLockDecorator);
        }

        if (realLock instanceof RLock) {
            return (RLock) lock;
        }
        throw new IllegalArgumentException("need RLock ，give lock not Present");
    }
}
