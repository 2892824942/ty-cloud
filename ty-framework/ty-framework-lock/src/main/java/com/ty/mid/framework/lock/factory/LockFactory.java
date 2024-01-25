package com.ty.mid.framework.lock.factory;

import com.ty.mid.framework.lock.enums.LockScopeType;

import java.util.concurrent.locks.Lock;

public interface LockFactory {
    Lock getLock(String type, String lockKey);

    LockScopeType getScopeType();



}