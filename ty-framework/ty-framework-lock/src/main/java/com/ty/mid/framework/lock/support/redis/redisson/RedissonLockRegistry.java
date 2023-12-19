package com.ty.mid.framework.lock.support.redis.redisson;

import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.registry.TypeLockRegistry;
import org.springframework.util.Assert;

import java.util.concurrent.locks.Lock;

public class RedissonLockRegistry implements TypeLockRegistry {
    LockFactory redissonLockFactory;


    public RedissonLockRegistry(LockFactory redissonLockFactory) {
        this.redissonLockFactory = redissonLockFactory;
    }

    public void setRedissonLockFactory(LockFactory redissonLockFactory) {
        this.redissonLockFactory = redissonLockFactory;
    }

    @Override
    public Lock obtain(Object lockKey) {
        return obtain(null, lockKey);
    }

    @Override
    public Lock obtain(String type, Object lockKey) {
        Assert.isInstanceOf(String.class, lockKey);
        String path = (String) lockKey;
        return redissonLockFactory.getLock(type, path);
    }
}
