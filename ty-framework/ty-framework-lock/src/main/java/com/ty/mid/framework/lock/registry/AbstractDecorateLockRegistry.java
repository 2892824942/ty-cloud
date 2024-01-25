package com.ty.mid.framework.lock.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.core.LockInfoProvider;
import com.ty.mid.framework.lock.decorator.LocalCacheLockDecorator;
import com.ty.mid.framework.lock.decorator.TransactionLockDecorator;
import com.ty.mid.framework.lock.enums.LockType;
import com.ty.mid.framework.lock.factory.LockFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

@Slf4j
public abstract class AbstractDecorateLockRegistry implements TypeLockRegistry {

    private final LockFactory lockFactory;
    @Resource
    private LockConfig lockConfig;

    @Resource
    private LockInfoProvider lockInfoProvider;

    public AbstractDecorateLockRegistry(LockFactory lockFactory) {
        this.lockFactory = lockFactory;

    }

    public LockFactory getLockFactory() {
        return lockFactory;
    }

    public Lock doGetLock(LockInfo lockInfo) {
        Lock lock = getLockFactory().getLock(Optional.ofNullable(lockInfo.getType()).map(LockType::getCode).orElse(null), lockInfo.getName());
        if (lockInfo.getWithLocalCache()) {
            lock = new LocalCacheLockDecorator(lock, lockInfo);
        }
        if (lockInfo.getSupportTransaction()) {
            lock = new TransactionLockDecorator(lock, lockInfo);
        }
        return lock;
    }

    @Override
    public Lock obtain(Object lockKey) {
        return this.obtain(null, lockKey);
    }

    @Override
    public Lock obtain(String type, Object lockKey) {
        Assert.isInstanceOf(String.class, lockKey);
        String lockKeyStr = lockKey.toString();
        LockInfo lockInfo = lockInfoProvider.transform2(lockConfig, type, lockKeyStr);
        return doGetLock(lockInfo);
    }
}
