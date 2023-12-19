package com.ty.mid.framework.lock.factory.support;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.factory.registry.AbstractTypeLockStrangeFactory;
import com.ty.mid.framework.lock.factory.registry.LockRegistryFactory;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.List;

public class TypeLockRegistryFactory implements LockRegistryFactory {
    public List<AbstractTypeLockStrangeFactory> typeRegistryFactories;

    public TypeLockRegistryFactory(List<AbstractTypeLockStrangeFactory> typeRegistryFactories) {
        this.typeRegistryFactories = typeRegistryFactories;
    }

    @Override
    public LockRegistry getLockRegistry(LockConfig.LockImplementer lockImplementer, boolean supportTransaction, boolean withLocalCache) {

        for (AbstractTypeLockStrangeFactory typeRegistryFactory : typeRegistryFactories) {
            if (lockImplementer.equals(typeRegistryFactory.implementerType()) && supportTransaction == typeRegistryFactory.supportTransaction() && withLocalCache == typeRegistryFactory.withLocalCache()) {
                return typeRegistryFactory.getLockRegistry();
            }
        }
        throw new UnsupportedOperationException("no support implementer of lockImplementer:" + lockImplementer + ",supportTransaction：" + supportTransaction + "，withLocalCache：" + withLocalCache);
    }

    @Override
    public LockFactory getLockFactory(LockConfig.LockImplementer lockImplementer, boolean supportTransaction, boolean withLocalCache) {
        for (AbstractTypeLockStrangeFactory typeRegistryFactory : typeRegistryFactories) {
            if (lockImplementer.equals(typeRegistryFactory.implementerType()) && supportTransaction == typeRegistryFactory.supportTransaction() && withLocalCache == typeRegistryFactory.withLocalCache()) {
                return typeRegistryFactory.getLockFactory();
            }
        }
        throw new UnsupportedOperationException("no support implementer of lockImplementer:" + lockImplementer + ",supportTransaction：" + supportTransaction + "，withLocalCache：" + withLocalCache);
    }
}
