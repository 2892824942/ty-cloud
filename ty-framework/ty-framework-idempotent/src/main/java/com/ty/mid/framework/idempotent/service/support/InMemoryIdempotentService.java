package com.ty.mid.framework.idempotent.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 基于内存的幂等校验服务 <p>
 * ATTENTION: this class is test only <p>
 * do not use this class in production <p>
 * @author suyouliang <p>
 * @createTime 2023-08-15 14:29 
 */
public class InMemoryIdempotentService extends AbstractIdempotentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, Long> executedMap = new ConcurrentHashMap<>();

    public InMemoryIdempotentService(LockRegistry lockRegistry) {
        super(lockRegistry);
    }

    public InMemoryIdempotentService(String lockKeyPrefix, LockRegistry lockRegistry) {
        super(lockKeyPrefix, lockRegistry);
    }

    public InMemoryIdempotentService(String lockKeyPrefix, long lockTimeout, TimeUnit lockTimeUnit, LockRegistry lockRegistry) {
        super(lockKeyPrefix, lockTimeout, lockTimeUnit, lockRegistry);
    }

    @Override
    protected boolean doCheckExecuted(String key) {
        return this.executedMap.containsKey(key);
    }

    @Override
    protected void doMark(String key) {
        log.info("mark executed in memory, redis key: {}", key);
        this.executedMap.put(key, System.currentTimeMillis());
    }

}
