package com.ty.mid.framework.lock.strategy;


import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.handler.release.ReleaseExceptionCustomerHandler;
import com.ty.mid.framework.lock.handler.release.ReleaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import java.util.concurrent.locks.Lock;

/**
 * @author 苏友良 <p>
 * @since 2022/4/15 <p>
 **/
@Slf4j
public enum ReleaseExceptionStrategy implements ReleaseExceptionHandler {

    /**
     * 用于区分默认值，调用者请勿使用
     */
    EMPTY() {
        @Override
        public void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint, Exception ex) {
            throw new FrameworkException("ReleaseTimeoutHandler.EMPTY is a type used to distinguish the default value and should not be used for actual business");
        }

        @Override
        public void handle(LockInfo lockInfo, Lock lock, Exception ex) {
            handle(lockInfo, lock, null, ex);
        }
    },
    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint, Exception ex) {
            // do nothing
        }

        @Override
        public void handle(LockInfo lockInfo, Lock lock, Exception ex) {
            handle(lockInfo, lock, null, ex);
        }
    },
    /**
     * 快速失败,可自定义异常类及报错信息
     */
    THROWING() {
        @Override
        public void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint, Exception ex) {

            String errorMsg = String.format("Found Lock(%s) already been released while lock lease time is %d s", lockInfo.getName(), lockInfo.getLeaseTime());
            log.warn(errorMsg);
            handleCustomException(lockInfo, errorMsg);
        }

        @Override
        public void handle(LockInfo lockInfo, Lock lock, Exception ex) {
            handle(lockInfo, lock, null, ex);
        }
    },

    /**
     * 自定义
     */
    CUSTOMER() {
        @Override
        public void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint, Exception ex) {
            ReleaseExceptionCustomerHandler releaseTimeoutCustomerHandler = super.getCustomerHandler(ReleaseExceptionCustomerHandler.class);
            releaseTimeoutCustomerHandler.handle(lockInfo, lock, joinPoint, ex);
        }

        @Override
        public void handle(LockInfo lockInfo, Lock lock, Exception ex) {
            ReleaseExceptionCustomerHandler releaseTimeoutCustomerHandler = super.getCustomerHandler(ReleaseExceptionCustomerHandler.class);
            releaseTimeoutCustomerHandler.handle(lockInfo, lock, ex);
        }
    }
}
