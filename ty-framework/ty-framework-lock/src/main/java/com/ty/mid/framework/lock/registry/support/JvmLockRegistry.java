package com.ty.mid.framework.lock.registry.support;

import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.support.JvmLockFactory;
import com.ty.mid.framework.lock.registry.AbstractCacheLockRegistry;

/**
 * 单例的本地lock注册
 */
public class JvmLockRegistry extends AbstractCacheLockRegistry {
    private static final LockFactory LOCK_FACTORY = JvmLockFactory.getInstance();
    private static final JvmLockRegistry JVM_LOCK_REGISTRY = new JvmLockRegistry(LOCK_FACTORY);


    public JvmLockRegistry(LockFactory LOCK_FACTORY) {
        super(LOCK_FACTORY);
    }

    public static JvmLockRegistry getInstance() {
        return JVM_LOCK_REGISTRY;
    }

    /**
     * 本地的lock是基于本地的cache实现，当cache存在过期时间时，过期前后相同key可能获取到不同的lock，从而导致锁不住的问题
     * 这里指定不允许做任何缓存过期逻辑
     *
     * @return
     */
    @Override
    protected boolean openClearLRU() {
        return true;
    }
}
