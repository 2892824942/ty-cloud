package com.ty.mid.framework.lock.support.redis.redisson;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.registry.AbstractTransactionSupportLocalCacheDistributedLockRegistry;

import java.util.concurrent.TimeUnit;

/**
 * 一：关键指标
 * 1.锁的碰撞率
 * 2.执行的速率
 * 3.性能损耗----主要是redis线程损耗，网络损耗
 * 二：经测试发现
 * <p>
 * 1.对应需要等待的锁，实际场景，带缓存的分布式锁并不会明显提升锁的碰撞率，但会明显降低redis的线程损耗和网络开销
 * <p>
 * 2.对于需要快速失败的锁，带缓存的分布式锁碰撞率提高（既然选择快速失败，此项指标不关心），明显降低redis的线程损耗和网络开销。执行速率提升4倍
 * <p>
 * 三：总结
 * 1.集群M台机器，单机单锁N并发下，可节省M*(N-1) 个redis线程（能有这种体量的项目不多），
 * 2.单机不存在并发度时，基本无差别
 * 3.快速失败场景额外提升4倍执行速率
 * 带本地缓存的分布式锁，发挥优势的业务场景非常少
 */
public class RedissonTransactionSupportWithLockCacheLockRegistry extends AbstractTransactionSupportLocalCacheDistributedLockRegistry {

    public RedissonTransactionSupportWithLockCacheLockRegistry(LockConfig lockConfig, LockFactory redissonLockFactory) {
        super(lockConfig, redissonLockFactory);
        super.timeUnit = TimeUnit.SECONDS;
        super.expiration = 2000;

    }

}
