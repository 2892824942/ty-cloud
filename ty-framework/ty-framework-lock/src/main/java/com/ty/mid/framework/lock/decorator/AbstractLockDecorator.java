package com.ty.mid.framework.lock.decorator;

import com.ty.mid.framework.lock.config.LockConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Slf4j
public abstract class AbstractLockDecorator implements Lock {


    protected String lockKey;
    protected LockConfig lockConfig;
    protected Lock distributedLock;
    private String prefix = "dec:lock";


    public AbstractLockDecorator(String lockKey, Lock distributedLock, LockConfig lockConfig) {
        this.lockKey = constructLockKey(lockKey);
        this.distributedLock = distributedLock;
        this.lockConfig = lockConfig;
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


}