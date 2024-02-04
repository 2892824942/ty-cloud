package com.ty.mid.framework.lock.strategy;


import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.handler.LockException;
import com.ty.mid.framework.lock.handler.lock.FailOnLockCustomerHandler;
import com.ty.mid.framework.lock.handler.lock.FailOnLockHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;


/**
 * @author 苏友良
 * @since 2022/4/15
 **/
@Slf4j
public enum FailOnLockStrategy implements FailOnLockHandler {
    /**
     * 空实现，区分默认值使用，使用者请勿使用
     */
    EMPTY() {
        @Override
        public boolean handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) {
            throw new FrameworkException("LockFailStrategy.EMPTY is a type used to distinguish the default value and should not be used for actual business");
        }
    },

    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public boolean handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) {
            // do nothing
            log.warn("LockTimeoutStrategy handle excute when lock fail ,lockInfo:{}", lockInfo);
            return false;
        }
    },

    /**
     * 快速失败
     */
    THROWING() {
        @Override
        public boolean handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) {

            String errorMsg = String.format("Failed to acquire Lock(%s) with timeout(%d ms)", lockInfo.getName(), lockInfo.getTimeUnit().toMillis(lockInfo.getWaitTime()));
            log.warn(errorMsg);
            handleCustomException(lockInfo, errorMsg);
            return false;
        }
    },

    /**
     * 一直阻塞，直到获得锁，在太多的尝试后，仍会报错
     */
    KEEP_ACQUIRE() {

        private static final long DEFAULT_INTERVAL = 100L;

        private static final long DEFAULT_MAX_INTERVAL = 3 * 60 * 1000L;

        @Override
        public boolean handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) {

            long interval = DEFAULT_INTERVAL;

            while (!lock.tryLock()) {

                if (interval > DEFAULT_MAX_INTERVAL) {
                    String errorMsg = String.format("Failed to acquire Lock(%s) after too many times, this may because dead lock occurs.",
                            lockInfo.getName());
                    throw new LockException(errorMsg);
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(interval);
                    //注意这里是位运算，每次*2,重试10次则 0.1s,0.2s,0.4s,0.8s,1.6s,3.2s,6.4s,12.8s,25.6s,51.2s
                    interval <<= 1;
                } catch (InterruptedException e) {
                    throw new LockException("Failed to acquire Lock", e);
                }
            }
            return true;
        }
    },

    /**
     * 自定义
     */
    CUSTOMER() {
        @Override
        public boolean handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) {
            FailOnLockCustomerHandler failOnLockCustomerHandler = super.getCustomerHandler(FailOnLockCustomerHandler.class);
            return failOnLockCustomerHandler.handle(lockInfo, lock, joinPoint);
        }
    }
}