package com.ty.mid.framework.idempotent.strategy;

/**
 * 幂等拒绝策略
 *
 * @author suyouliang
 * @createTime 2019-08-15 14:22
 */
public enum IdempotentRejectStrategy {

    /**
     * 抛异常
     */
    THROW_EXCEPTION,
    /**
     * 将幂等检查结果写进上下文
     */
    PUSH_CONTEXT

}
