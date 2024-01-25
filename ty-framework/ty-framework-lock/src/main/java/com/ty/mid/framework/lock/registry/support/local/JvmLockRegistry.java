package com.ty.mid.framework.lock.registry.support.local;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.support.LocalLockFactory;
import com.ty.mid.framework.lock.registry.AbstractCacheLockRegistry;

/**
 * 单例的本地lock注册
 */
public class JvmLockRegistry extends AbstractCacheLockRegistry {
    private static LockFactory lockFactory = new LocalLockFactory();
    private static JvmLockRegistry JVMLockRegistry = new JvmLockRegistry( lockFactory);


    public JvmLockRegistry(LockFactory lockFactory) {
        super(lockFactory);
    }

    public static JvmLockRegistry getInstance() {
        return JVMLockRegistry;
    }

    /**
     * 本地的lock是基于本地的cache实现，当cache存在过期时间时，过期前后相同key可能获取到不同的lock，从而导致锁不住的问题
     * 这里指定不允许做任何缓存过期逻辑
     *
     * @return
     */
    @Override
    protected boolean openClearLRU() {
        return false;
    }
}
