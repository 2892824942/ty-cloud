package com.ty.mid.framework.idempotent.strategy;

/**
 * 幂等拒绝策略 <p>
 *
 * @author suyouliang <p>
 * @createTime 2023-08-15 14:22
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
