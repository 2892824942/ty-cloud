package com.ty.mid.framework.lock.annotation;


import com.ty.mid.framework.lock.config.LockConfig;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author suyouliang <p>
 * @date 2022/03/26 <p>
 * Content :加锁注解 
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Lock
@Documented
public @interface LocalLock {
    /**
     * 锁的名称
     *
     * @return name
     */
    String name() default "";

    /**
     * 自定义业务key
     *
     * @return keys
     */
    String[] keys() default {};

    /**
     * 尝试加锁，最多等待时间,不设置将使用配置指定的时间
     *
     * @return waitTime
     * @see LockConfig#getWaitTime()
     * 如果配置没有指定此时间，默认2s
     */
    long waitTime() default Long.MIN_VALUE;

    /**
     * 时间单位，此参数作用：waitTime及leaseTime
     *
     * @return leaseTime
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;


}
