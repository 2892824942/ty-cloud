package com.ty.mid.framework.lock.factory;

import com.ty.mid.framework.lock.adapter.LockAdapter;

/**
 * 对于工厂生产的lock，自带lock适配器，允许lock自己定义自己的加锁解锁方法
 */
public interface AdapterLockFactory extends LockFactory {
    LockAdapter getAdapter();
}
