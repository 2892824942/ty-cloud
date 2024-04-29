package com.ty.mid.framework.idempotent.service.support;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.idempotent.constant.IdempotentConstant;
import com.ty.mid.framework.idempotent.exception.AlreadyExecutedIdempotentException;
import com.ty.mid.framework.idempotent.exception.IdempotentServiceException;
import com.ty.mid.framework.idempotent.service.IdempotentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 抽象幂等校验服务 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-15 14:36 
 */
@Slf4j
public abstract class AbstractIdempotentService implements IdempotentService {

    // 前缀
    private String lockKeyPrefix = IdempotentConstant.DEFAULT_LOCK_KEY_PREFIX;
    // 获取锁超时时间
    private long lockTimeout = IdempotentConstant.DEFAULT_LOCK_TIMEOUT;
    // 获取锁超时单位
    private TimeUnit lockTimeUnit = IdempotentConstant.DEFAULT_LOCK_TIME_UNIT;

    // 锁注册中心
    private LockRegistry lockRegistry;

    public AbstractIdempotentService(LockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

    public AbstractIdempotentService(String lockKeyPrefix, LockRegistry lockRegistry) {
        this.lockKeyPrefix = lockKeyPrefix;
        this.lockRegistry = lockRegistry;
    }

    public AbstractIdempotentService(String lockKeyPrefix, long lockTimeout, TimeUnit lockTimeUnit, LockRegistry lockRegistry) {
        this.lockKeyPrefix = lockKeyPrefix;
        this.lockTimeout = lockTimeout;
        this.lockTimeUnit = lockTimeUnit;
        this.lockRegistry = lockRegistry;
    }

    @Override
    public void markServiceExecuted(String name) throws IdempotentServiceException {
        // check not executed
        this.ensureNotExecuted(name);

        String lockKey = this.resolveLockKey(name);

        Lock lock = this.lockRegistry.obtain(lockKey);

        log.info("trying obtain lock, lock key: {}, timeout: {}, timeUnit: {}", lockKey, this.lockTimeout, this.lockTimeUnit);
        try {
            if (lock.tryLock(this.lockTimeout, this.lockTimeUnit)) {
                // double check not executed again
                this.ensureNotExecuted(lockKey);

                this.doMark(lockKey);
            }
        } catch (AlreadyExecutedIdempotentException e) {
            throw e;
        } catch (Exception e) {
            log.warn("mark {} executed error, error message: {}", name, e.getMessage());
            throw new IdempotentServiceException(e);
        } finally {
            // close lock
            lock.unlock();
        }

    }

    @Override
    public boolean isServiceExecuted(String name) {
        return this.doCheckExecuted(this.resolveLockKey(name));
    }

    protected abstract boolean doCheckExecuted(String key);

    /**
     * 写入数据，设置服务已经执行过
     *
     * @param key
     */
    protected abstract void doMark(String key);

    protected String resolveLockKey(String name) {
        return StrUtil.isEmpty(lockKeyPrefix) ? name : this.lockKeyPrefix.concat(name);
    }

    /**
     * 确保服务没有执行过
     *
     * @param name
     */
    protected void ensureNotExecuted(String name) {
        if (this.isServiceExecuted(name)) {
            throw new AlreadyExecutedIdempotentException();
        }
    }
}
