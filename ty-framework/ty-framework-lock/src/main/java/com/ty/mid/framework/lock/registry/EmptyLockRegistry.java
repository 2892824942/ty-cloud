package com.ty.mid.framework.lock.registry;

import com.ty.mid.framework.common.exception.FrameworkException;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.concurrent.locks.Lock;

public class EmptyLockRegistry implements LockRegistry {

    @Override
    public Lock obtain(Object lockKey) {
        throw new FrameworkException("EmptyLockRegistry is a type used to distinguish the default value and should not be used for actual business");
    }
}
