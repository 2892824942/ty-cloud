package com.ty.mid.framework.lock.registry;

import org.springframework.integration.support.locks.LockRegistry;

import java.lang.annotation.Native;

public final class LockRegistryConstant {
    @Native
    public static final LockRegistry EMPTY_LOCK_REGISTRY = new EmptyLockRegistry();
}
