package com.ty.mid.framework.lock.decorator;

import com.ty.mid.framework.lock.core.LockInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.util.StringUtils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Slf4j
public abstract class AbstractLockDecorator implements Lock {


    protected LockInfo lockInfo;
    @Getter
    protected Lock realLock;
    private String prefix = "dec:lock";


    public AbstractLockDecorator(Lock realLock, LockInfo lockInfo) {
        this.realLock = realLock;
        this.lockInfo = lockInfo;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private String constructLockKey(String path) {
        return StringUtils.isEmpty(prefix) ? path : prefix + ':' + path;
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Conditions are not supported");
    }

    protected void rethrowAsLockException(Exception e) {
        throw new CannotAcquireLockException("Failed to lock mutex at " + this.lockInfo.getName(), e);
    }


}