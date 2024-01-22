package com.ty.mid.framework.lock.factory.registry;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import org.springframework.integration.support.locks.LockRegistry;

public interface LockRegistryFactory {
    //TODO 优化掉 supportTransaction withLocalCache 参数,在代码内部判断,这两者是装饰的能力,不应通过参数定义,写死了.
    LockRegistry getLockRegistry(LockConfig.LockImplementer lockImplementer, boolean supportTransaction, boolean withLocalCache);

    LockFactory getLockFactory(LockConfig.LockImplementer lockImplementer, boolean supportTransaction, boolean withLocalCache);

}