package com.ty.mid.framework.idempotent.annotation;

import com.ty.mid.framework.idempotent.strategy.IdempotentRejectStrategy;

import java.lang.annotation.*;

/**
 * 幂等校验接口 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-15 14:19
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    String value();

    /**
     * 默认策略，抛异常
     *
     * @return
     */
    IdempotentRejectStrategy rejectStrategy() default IdempotentRejectStrategy.THROW_EXCEPTION;
}
