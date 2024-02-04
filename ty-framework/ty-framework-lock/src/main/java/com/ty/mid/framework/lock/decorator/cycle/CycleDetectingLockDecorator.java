package com.ty.mid.framework.lock.decorator.cycle;

import cn.hutool.core.collection.CollUtil;
import com.ty.mid.framework.core.cache.Cache;
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

    private static final Cache<String, LockGraphNode> LOCK_GRAPH_NODE_CACHE = new InMemoryCache<>(true);

    private final LockGraphNode lockGraphNode;

    public CycleDetectingLockDecorator(Lock distributedLock, LockInfo lockInfo) {
        super(distributedLock, lockInfo);
        this.lockGraphNode = LOCK_GRAPH_NODE_CACHE.computeIfAbsent(lockInfo.getName(), new LockGraphNode(lockInfo.getName()), TimeUnit.MINUTES, 10L);
    }

    /**
     * CycleDetectingLock implementations must call this method in a {@code finally} clause after any
     * attempt to change the lock state, including both lock and unlock attempts. Failure to do so can
     * result in corrupting the acquireLocks set.
     */
    private static void lockStateChanged(CycleDetectingLockDecorator lock) {
    }

    @Override
    public void lock() {
        try {
            this.realLock.lock();
            //加锁成功后进行链路图保存
            aboutToAcquire();
        } finally {
            lockStateChanged(this);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            this.realLock.lockInterruptibly();
            //加锁成功后进行链路图保存
            aboutToAcquire();
        } catch (InterruptedException interruptedException) {
            //如果分布式锁实现没有做兜底,这里做兜底
            this.realLock.unlock();
            Thread.currentThread().interrupt();
            throw interruptedException;
        } catch (Exception e) {
            this.realLock.unlock();
            this.rethrowAsLockException(e);
        } finally {
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
        } finally {
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
            boolean b = realLock.tryLock(time, unit);
            //加锁成功后进行链路图保存
            aboutToAcquire();
            return b;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lockStateChanged(this);
        }
    }

    @Override
    public void unlock() {
        try {
            realLock.unlock();
        } finally {
            lockStateChanged(this);
        }

    }

    /**
     * CycleDetectingLock implementations must call this method before attempting to acquire the lock.
     */
    private void aboutToAcquire() {
        Collection<LockGraphNode> acquiredLockList = LOCK_GRAPH_NODE_CACHE.getAll();
        if (CollUtil.isEmpty(acquiredLockList)) {
            return;
        }
        lockGraphNode.checkAcquiredLocks(super.lockInfo.getCycleLockStrategy(), new ArrayList<>(acquiredLockList));
        //lockGraphNodeCache.(lockGraphNode);
    }

}