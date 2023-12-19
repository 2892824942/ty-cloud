package com.ty.mid.framework.lock.model;


import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.lock.handler.release.ReleaseTimeoutCustomerHandler;
import com.ty.mid.framework.lock.handler.release.ReleaseTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 苏友良
 * @since 2022/4/15
 **/
@Slf4j
public enum ReleaseTimeoutStrategy implements ReleaseTimeoutHandler {

    /**
     * 用于区分默认值，调用者请勿使用
     */
    EMPTY() {
        @Override
        public void handle(LockInfo lockInfo) {
            throw new FrameworkException("ReleaseTimeoutHandler.EMPTY is a type used to distinguish the default value and should not be used for actual business");
        }
    },
    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo) {
            // do nothing
        }
    },
    /**
     * 快速失败,可自定义异常类及报错信息
     */
    FAIL_FAST() {
        @Override
        public void handle(LockInfo lockInfo) {

            String errorMsg = String.format("Found Lock(%s) already been released while lock lease time is %d s", lockInfo.getName(), lockInfo.getLeaseTime());
            log.warn(errorMsg);
            handleCustomException(lockInfo, errorMsg);
        }
    },

    /**
     * 自定义
     */
    CUSTOMER() {
        @Override
        public void handle(LockInfo lockInfo) {
            ReleaseTimeoutCustomerHandler releaseTimeoutCustomerHandler = super.getCustomerHandler(ReleaseTimeoutCustomerHandler.class);
            releaseTimeoutCustomerHandler.handle(lockInfo);
        }
    }
}
