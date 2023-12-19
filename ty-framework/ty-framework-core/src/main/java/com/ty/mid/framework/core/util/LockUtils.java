package com.ty.mid.framework.core.util;

import com.ty.mid.framework.common.constant.LockConstant;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.lang.Nullable;
import com.ty.mid.framework.common.lang.ThreadSafe;
import com.ty.mid.framework.common.util.Validator;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 锁工具类
 */
@Slf4j
@ThreadSafe
public class LockUtils {

    /**
     * 关闭锁
     *
     * @param lock
     */
    public static void unlockQuietly(@Nullable Lock lock) {
        if (lock == null) {
            log.info("can not unlock lock because lock is null!");
            return;
        }

        try {
            lock.unlock();
        } catch (Exception e) {
            log.warn("unlock lock with error, error message: {}", e.getMessage());
        }
    }

    /**
     * 尝试获取锁，默认等待时间2s
     *
     * @param lock
     * @return
     */
    public static boolean ensureObtain(Lock lock) {
        try {
            boolean locked = lock.tryLock(LockConstant.DEFAULT_TIMEOUT, LockConstant.DEFAULT_TIME_UNIT);
            Validator.requireTrue(locked, "操作过于频繁，请稍后再试");
        } catch (InterruptedException e) {
            log.warn("获取分布式锁失败", e);
            throw new FrameworkException("操作过于频繁，请稍后再试");
        }
        return true;
    }

    /**
     * 尝试获取锁，可自定义传入时间
     * 一般情况下，锁的等待时间不应超过2s
     *
     * @param lock
     * @param time
     * @param unit
     * @return
     */
    public static boolean ensureObtain(Lock lock, long time, TimeUnit unit) {
        try {
            boolean locked = lock.tryLock(time, unit);
            Validator.requireTrue(locked, "操作过于频繁，请稍后再试");
        } catch (InterruptedException e) {
            log.warn("获取分布式锁失败", e);
            throw new FrameworkException("操作过于频繁，请稍后再试");
        }
        return true;
    }
}
