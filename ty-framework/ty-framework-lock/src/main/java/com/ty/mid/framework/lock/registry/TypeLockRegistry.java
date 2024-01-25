package com.ty.mid.framework.lock.registry;

import com.ty.mid.framework.lock.core.LockInfo;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.concurrent.locks.Lock;

public interface TypeLockRegistry extends LockRegistry {

    /**
     * 可指定类型的锁注册中心
     */
    Lock obtain(String type, Object lockKey);
}
