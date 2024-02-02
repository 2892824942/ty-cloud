package com.ty.mid.framework.lock.decorator.cycle;

import cn.hutool.cache.impl.LFUCache;
import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.CycleDetectingLockFactory;
import com.ty.mid.framework.core.cache.Cache;
import com.ty.mid.framework.core.cache.HashCache;
import com.ty.mid.framework.core.cache.support.InMemoryCache;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.decorator.AbstractLockDecorator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
public class CycleDetectingLockDecorator extends AbstractLockDecorator {
    private final Policy policy = Policies.THROW;

    private static final Cache<String,LockGraphNode> lockGraphNodeCache=new InMemoryCache<>(true);

    private LockGraphNode lockGraphNode;

    public CycleDetectingLockDecorator(Lock distributedLock, LockInfo lockInfo) {
        super(distributedLock, lockInfo);
        LockGraphNode lockGraphNode = lockGraphNodeCache.computeIfAbsent(lockInfo.getName(), new LockGraphNode(lockInfo.getName()), TimeUnit.MINUTES, 10L);
        this.lockGraphNode = lockGraphNode;
    }

    @Override
    public void lock() {
        try {
            this.distributedLock.lock();
            //加锁成功后进行链路图保存
            aboutToAcquire();
        }finally {
            lockStateChanged(this);
        }
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            this.distributedLock.lockInterruptibly();
            //加锁成功后进行链路图保存
            aboutToAcquire();
        } catch (InterruptedException interruptedException) {
            //如果分布式锁实现没有做兜底,这里做兜底
            this.distributedLock.unlock();
            Thread.currentThread().interrupt();
            throw interruptedException;
        } catch (Exception e) {
            this.distributedLock.unlock();
            this.rethrowAsLockException(e);
        }finally {
            lockStateChanged(this);
        }
    }

    @Override
    public boolean tryLock() {
        try {
            boolean b = tryLock(0, TimeUnit.MILLISECONDS);
            //加锁成功后进行链路图保存
            aboutToAcquire();
            return b;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }finally {
            lockStateChanged(this);
        }
    }

    /**
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        try {
            boolean b = tryLock(time, unit);
            //加锁成功后进行链路图保存
            aboutToAcquire();
            return b;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }finally {
            lockStateChanged(this);
        }
    }


    @Override
    public void unlock() {
        try {
            distributedLock.unlock();
        } finally {
            lockStateChanged(this);
        }

    }

    /**
     * CycleDetectingLock implementations must call this method before attempting to acquire the lock.
     */
    private void aboutToAcquire() {
        Collection<LockGraphNode> acquiredLockList = lockGraphNodeCache.getAll();
        if (CollUtil.isEmpty(acquiredLockList)){
            return;
        }
        lockGraphNode.checkAcquiredLocks(policy, new ArrayList<>(acquiredLockList));
        //lockGraphNodeCache.(lockGraphNode);
    }

    /**
     * CycleDetectingLock implementations must call this method in a {@code finally} clause after any
     * attempt to change the lock state, including both lock and unlock attempts. Failure to do so can
     * result in corrupting the acquireLocks set.
     */
    private static void lockStateChanged(CycleDetectingLockDecorator lock) {
    }

}