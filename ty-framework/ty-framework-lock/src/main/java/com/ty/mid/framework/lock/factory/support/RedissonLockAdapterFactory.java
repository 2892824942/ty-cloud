package com.ty.mid.framework.lock.factory.support;

import com.ty.mid.framework.lock.adapter.LockAdapter;
import com.ty.mid.framework.lock.adapter.support.RedissonLockAdapter;
import com.ty.mid.framework.lock.factory.AdapterLockFactory;
import org.redisson.api.RedissonClient;

public class RedissonLockAdapterFactory extends RedissonDefaultLockFactory implements AdapterLockFactory {

    public RedissonLockAdapterFactory(RedissonClient redissonClient) {
        super(redissonClient);
    }

    @Override
    public LockAdapter getAdapter() {
        return new RedissonLockAdapter();
    }
}
