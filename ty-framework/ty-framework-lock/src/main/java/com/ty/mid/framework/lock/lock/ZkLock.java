package com.ty.mid.framework.lock.lock;

import com.ty.mid.framework.common.exception.FrameworkException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessLock;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Data
@Slf4j
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
            log.error("get lock error,impl:zk,error:",e);
            throw new FrameworkException("get lock error,impl:zk,error:");
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
            log.error("get lock error,impl:zk,error:",e);
            throw new FrameworkException("get lock error,impl:zk");
        }

    }

    @Override
    public boolean tryLock() {
        try {
            interProcessLock.acquire();
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("get tryLock error,impl:zk,error:",e);
            throw new FrameworkException("get lock error,impl:zk");
        }
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        try {
            return interProcessLock.acquire(time, unit);
        } catch (Exception e) {
            log.error("get lock error,impl:zk,error:",e);
            throw new FrameworkException("get lock error,impl:zk");
        }

    }

    @Override
    public void unlock() {
        try {
            interProcessLock.release();
        } catch (Exception e) {
            log.error("release lock error,impl:zk,error:",e);
            throw new FrameworkException("release lock error,impl:zk");
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Conditions are not supported");
    }
}
