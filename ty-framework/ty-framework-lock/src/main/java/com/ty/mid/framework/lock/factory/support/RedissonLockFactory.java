package com.ty.mid.framework.lock.factory.support;

import com.ty.mid.framework.lock.enums.LockScopeType;
import com.ty.mid.framework.lock.enums.LockType;
import com.ty.mid.framework.lock.factory.LockFactory;
import lombok.Getter;
import org.redisson.api.RedissonClient;

import java.util.Objects;
import java.util.concurrent.locks.Lock;

@Getter
public class RedissonLockFactory implements LockFactory {
    protected RedissonClient redissonClient;

    public RedissonLockFactory(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Lock getLock(String type, String lockKey) {

        LockType lockTypeEnum = LockType.of(type);
        if (Objects.isNull(lockTypeEnum)) {
            return redissonClient.getLock(lockKey);
        }
        switch (lockTypeEnum) {
            case Fair:
                return redissonClient.getFairLock(lockKey);
            case Read:
                return redissonClient.getReadWriteLock(lockKey).readLock();
            case Write:
                return redissonClient.getReadWriteLock(lockKey).writeLock();
            case Reentrant:
            default:
                return redissonClient.getLock(lockKey);
        }
    }

    @Override
    public LockScopeType getScopeType() {
        return LockScopeType.Distributed;
    }

}
