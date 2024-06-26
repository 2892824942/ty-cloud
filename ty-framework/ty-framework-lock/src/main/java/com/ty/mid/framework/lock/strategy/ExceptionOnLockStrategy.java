package com.ty.mid.framework.lock.strategy;


import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.exception.LockException;
import com.ty.mid.framework.lock.handler.lock.ExceptionOnLockCustomerHandler;
import com.ty.mid.framework.lock.handler.lock.ExceptionOnLockHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import java.util.concurrent.locks.Lock;


/**
 * @author 苏友良 <p>
 * @since 2022/4/15 <p>
 **/
@Slf4j
public enum ExceptionOnLockStrategy implements ExceptionOnLockHandler {
    /**
     * 空实现，区分默认值使用，使用者请勿使用
     */
    EMPTY() {
        @Override
        public Object handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint, Exception ex) {
            throw new FrameworkException("LockFailStrategy.EMPTY is a type used to distinguish the default value and should not be used for actual business");
        }

        @Override
        public Object handle(LockInfo lockInfo, Lock lock, Exception ex) {
            return handle(lockInfo, lock, null, ex);
        }
    },


    /**
     * 抛出异常
     */
    THROWING() {
        @Override
        public Object handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint, Exception ex) {
            log.warn("Exception to acquire Lock:{}", lockInfo.getName(), ex);
            throw new LockException(ex);
        }

        @Override
        public Object handle(LockInfo lockInfo, Lock lock, Exception ex) {
            return handle(lockInfo, lock, null, ex);
        }
    },


    /**
     * 自定义
     */
    CUSTOMER() {
        @Override
        public Object handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint, Exception ex) {
            ExceptionOnLockCustomerHandler exceptionOnLockCustomerHandler = super.getCustomerHandler(ExceptionOnLockCustomerHandler.class);
            return exceptionOnLockCustomerHandler.handle(lockInfo, lock, joinPoint, ex);
        }

        @Override
        public Object handle(LockInfo lockInfo, Lock lock, Exception ex) {
            ExceptionOnLockCustomerHandler exceptionOnLockCustomerHandler = super.getCustomerHandler(ExceptionOnLockCustomerHandler.class);
            return exceptionOnLockCustomerHandler.handle(lockInfo, lock, ex);
        }
    }
}