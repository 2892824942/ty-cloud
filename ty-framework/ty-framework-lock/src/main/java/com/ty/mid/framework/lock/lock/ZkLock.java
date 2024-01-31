package com.ty.mid.framework.lock.lock;

import com.ty.mid.framework.common.exception.FrameworkException;
import lombok.Data;
import org.apache.curator.framework.recipes.locks.InterProcessLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Data
public class ZkLock implements Lock {
    private InterProcessLock interProcessLock;

    public ZkLock(InterProcessLock interProcessLock) {
        this.interProcessLock = interProcessLock;
    }

    @Override
    public void lock() {
        try {
            interProcessLock.acquire();
        } catch (Exception e) {
            throw new FrameworkException("get lock error,impl:zk,error:", e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            interProcessLock.acquire();
        } catch (InterruptedException interruptedException) {
            if (interProcessLock.isAcquiredInThisProcess()) {
                try {
                    interProcessLock.release();
                } catch (Exception e) {
                    throw new FrameworkException("release lock error when InterruptedException,impl:zk,error:");
                }
            }
        } catch (Exception e) {
            throw new FrameworkException("get lock error,impl:zk,error:", e);
        }

    }

    @Override
    public boolean tryLock() {
        try {
            interProcessLock.acquire();
            return Boolean.TRUE;
        } catch (Exception e) {
            throw new FrameworkException("get lock error,impl:zk,error:", e);
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        try {
            return interProcessLock.acquire(time, unit);
        } catch (Exception e) {
            throw new FrameworkException("get lock error,impl:zk,error:", e);
        }

    }

    @Override
    public void unlock() {
        try {
            interProcessLock.release();
        } catch (Exception e) {
            throw new FrameworkException("release lock error,impl:zk,error:", e);
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Conditions are not supported");
    }
}
