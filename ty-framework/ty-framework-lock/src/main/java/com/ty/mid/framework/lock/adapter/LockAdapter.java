package com.ty.mid.framework.lock.adapter;

import com.ty.mid.framework.lock.core.LockInfo;

import java.util.concurrent.locks.Lock;

/**
 * Created by suyouliang
 * 存在不同的Java Lock的实现，有更加强大的功能。直接基于Lock的定义调用过于僵硬（比如tryLock没有最大持有时间，而redis是有这种功能的）。
 * 此类为Lock的适配器，对于加锁和上锁定义
 * 具体的Lock实现可基于此接口，定制自己的加锁和解锁的逻辑
 */

public interface LockAdapter {
    /**
     * 等待和持有时间都设置
     * @param lock
     * @param lockInfo
     * @return
     */
    boolean acquire(Lock lock, LockInfo lockInfo);

    /**
     * 等待和持有时间都不设置
     *
     * @param lock
     * @param lockInfo
     * @return
     */
    boolean acquireNoTime(Lock lock, LockInfo lockInfo);
    /**
     * 等待时间不设置
     *
     * @param lock
     * @param lockInfo
     * @return
     */
    boolean acquireNoWaitTime(Lock lock, LockInfo lockInfo);

    boolean acquireInterruptibly(Lock lock, LockInfo lockInfo) throws InterruptedException;

    boolean release(Lock lock, LockInfo lockInfo);
}

