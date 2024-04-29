package com.ty.mid.framework.idempotent.service.support;

import com.ty.mid.framework.idempotent.constant.IdempotentConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.concurrent.TimeUnit;

/**
 * 基于redis 的幂等校验服务 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-15 14:29
 */
public class RedisIdempotentService extends AbstractIdempotentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private long ttlInSeconds = IdempotentConstant.DEFAULT_TTL_SECONDS;
    private StringRedisTemplate redisTemplate;

    public RedisIdempotentService(LockRegistry lockRegistry, StringRedisTemplate redisTemplate) {
        super(lockRegistry);
        this.redisTemplate = redisTemplate;
    }

    public RedisIdempotentService(String lockKeyPrefix, LockRegistry lockRegistry, StringRedisTemplate redisTemplate) {
        super(lockKeyPrefix, lockRegistry);
        this.redisTemplate = redisTemplate;
    }

    public RedisIdempotentService(String lockKeyPrefix, long lockTimeout, TimeUnit lockTimeUnit, LockRegistry lockRegistry, StringRedisTemplate redisTemplate) {
        super(lockKeyPrefix, lockTimeout, lockTimeUnit, lockRegistry);
        this.redisTemplate = redisTemplate;
    }

    public RedisIdempotentService(String lockKeyPrefix, long lockTimeout, TimeUnit lockTimeUnit, long ttlInSeconds, LockRegistry lockRegistry, StringRedisTemplate redisTemplate) {
        super(lockKeyPrefix, lockTimeout, lockTimeUnit, lockRegistry);
        this.redisTemplate = redisTemplate;
        this.ttlInSeconds = ttlInSeconds;
    }

    @Override
    protected void doMark(String key) {
        log.info("mark {} executed in redis, redis key: {}, ttl: {}", key, key, ttlInSeconds);
        String nowMillis = String.valueOf(System.currentTimeMillis());
        if (ttlInSeconds <= 0) {
            this.redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()));
        } else {

            redisTemplate.opsForValue().set(key, nowMillis, ttlInSeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    protected boolean doCheckExecuted(String key) {
        return this.redisTemplate.hasKey(key);
    }
}
