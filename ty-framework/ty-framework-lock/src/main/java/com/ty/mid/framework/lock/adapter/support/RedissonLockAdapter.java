package com.ty.mid.framework.lock.adapter.support;

import com.ty.mid.framework.lock.adapter.LockAdapter;
import com.ty.mid.framework.lock.model.LockInfo;
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
            return false;
        }
    }


    @Override
    public boolean release(Lock lock, LockInfo lockInfo) {
        RLock rLock = this.convert2RLock(lock);
        if (rLock.isHeldByCurrentThread()) {
            try {
                return rLock.forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                log.warn("release lock have InterruptedException | ExecutionException e:", e);
                return false;
            }
        }
        return false;
    }

    private RLock convert2RLock(Lock lock) {
        if (lock instanceof RLock) {
            return (RLock) lock;
        }
        throw new IllegalArgumentException("need RLock ，give lock not Present");
    }
}
