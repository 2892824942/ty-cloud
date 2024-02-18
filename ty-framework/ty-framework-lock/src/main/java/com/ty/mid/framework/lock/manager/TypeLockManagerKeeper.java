package com.ty.mid.framework.lock.manager;

import com.ty.mid.framework.lock.enums.LockImplementer;
import com.ty.mid.framework.lock.factory.LockFactory;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.List;

public class TypeLockManagerKeeper implements LockManagerKeeper {
    public List<AbstractTypeLockManager> typeRegistryFactories;

    public TypeLockManagerKeeper(List<AbstractTypeLockManager> typeRegistryFactories) {
        this.typeRegistryFactories = typeRegistryFactories;
    }

    @Override
    public LockRegistry getLockRegistry(LockImplementer lockImplementer) {

        for (AbstractTypeLockManager typeRegistryFactory : typeRegistryFactories) {
            if (lockImplementer.equals(typeRegistryFactory.implementerType())) {
                return typeRegistryFactory.getLockRegistry();
            }
        }
        throw new UnsupportedOperationException("no support implementer of lockImplementer:" + lockImplementer);
    }

    @Override
    public LockFactory getLockFactory(LockImplementer lockImplementer) {
        for (AbstractTypeLockManager typeRegistryFactory : typeRegistryFactories) {
            if (lockImplementer.equals(typeRegistryFactory.implementerType())) {
                return typeRegistryFactory.getLockFactory();
            }
        }
        throw new UnsupportedOperationException("no support implementer of lockImplementer:");
    }

    @Override
    public List<AbstractTypeLockManager> getTypeLockManagers() {
        return typeRegistryFactories;
    }
}
