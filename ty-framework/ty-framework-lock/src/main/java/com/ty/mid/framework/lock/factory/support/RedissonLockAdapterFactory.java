package com.ty.mid.framework.lock.factory.support;

import com.ty.mid.framework.lock.adapter.LockAdapter;
import com.ty.mid.framework.lock.adapter.support.RedissonLockAdapter;
import com.ty.mid.framework.lock.factory.AdapterLockFactory;
import org.redisson.api.RedissonClient;

/**
 * 需要使用Redisson方言的失效时间时,使用此Factory
 */
public class RedissonLockAdapterFactory extends RedissonLockFactory implements AdapterLockFactory {

    private final LockAdapter redissonLockAdapter = new RedissonLockAdapter();

    public RedissonLockAdapterFactory(RedissonClient redissonClient) {
        super(redissonClient);
    }

    @Override
    public LockAdapter getAdapter() {
        return redissonLockAdapter;
    }

}
