package com.ty.mid.framework.lock.annotation;


import com.ty.mid.framework.common.constant.BooleanEnum;
import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.enums.LockImplementer;
import com.ty.mid.framework.lock.enums.LockType;
import com.ty.mid.framework.lock.strategy.ExceptionOnLockStrategy;
import com.ty.mid.framework.lock.strategy.FailOnLockStrategy;
import com.ty.mid.framework.lock.strategy.ReleaseExceptionStrategy;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author suyouliang
 * @date 2022/03/26
 * Content :加锁注解
 * 存在本类定义，且lockConfig存在同样定义时，本类定义优先级更高
 * 当使用空值时，则使用全局的lockConfig设定的值
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Lock {
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
     * 锁的厂商实现
     *
     * @return LockConfig.LockImplementer
     */
    LockImplementer implementer() default LockImplementer.EMPTY;

    /**
     * 锁类型，默认可重入锁
     *
     * @return lockType
     */
    LockType lockType() default LockType.Reentrant;

    /**
     * 尝试加锁，最多等待时间,不设置将使用配置指定的时间
     *
     * @return waitTime
     * @see LockConfig#getWaitTime()
     * 如果配置没有指定此时间，默认2s
     */
    long waitTime() default Long.MIN_VALUE;


    /**
     * 上锁以后xxx秒自动解锁,不设置将使用配置指定的时间
     *
     * @return leaseTime
     * @see LockConfig#getLeaseTime()
     * 如果配置没有指定此时间，默认60s
     */
    long leaseTime() default Long.MIN_VALUE;

    /**
     * 时间单位，此参数作用：waitTime及leaseTime
     *
     * @return leaseTime
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 是否支持本地lock二级缓存
     */
    BooleanEnum withLocalCache() default BooleanEnum.NULL;

    /**
     * 加锁失败的处理策略
     * 如果此处不设置，取LockConfig设置的值
     * 优先级：customLockFailStrategy>lockFailStrategy>lockConfig
     *
     * @return lockTimeoutStrategy
     */
    FailOnLockStrategy lockFailStrategy() default FailOnLockStrategy.EMPTY;


    /**
     * 加锁失败的处理策略
     * 注意：定义的方法参数需要和注解所在的方法参数保持一致
     * 此方式与lockFailStrategy中的CUSTOM不同点在于：此方式可对方法具体参数做定制化的处理策略，后者更适合做全局的默认处理
     * 优先级：customLockFailStrategy>lockFailStrategy>lockConfig
     *
     * @return String
     */
    String customLockFailStrategy() default "";

    /**
     * 加锁异常的处理策略
     * 如果此处不设置，取LockConfig设置的值
     * 优先级：lockExceptionStrategy>lockConfig
     *
     * @return ExceptionOnLockStrategy
     */
    ExceptionOnLockStrategy lockExceptionStrategy() default ExceptionOnLockStrategy.EMPTY;


    /**
     * 释放锁时已超时的处理策略
     * 如果此处不设置，取LockConfig设置的值
     * 优先级：customReleaseTimeoutStrategy>releaseTimeoutStrategy>lockConfig
     *
     * @return releaseTimeoutStrategy
     */
    ReleaseExceptionStrategy releaseTimeoutStrategy() default ReleaseExceptionStrategy.EMPTY;


    /**
     * 自定义释放锁时已超时的处理策略
     * 注意：定义的方法参数需要和注解所在的方法参数保持一致
     * 此方式与releaseTimeoutStrategy中的CUSTOM不同点在于：此方式可对方法具体参数做定制化的处理策略，后者更适合做全局的默认处理
     * 优先级：customReleaseTimeoutStrategy>releaseTimeoutStrategy>lockConfig
     *
     * @return String
     */
    String customReleaseTimeoutStrategy() default "";

    /**
     * 获取锁失败时，报错的异常类型
     * 仅当LockFailStrategy.FAIL_FAST或者ReleaseTimeoutStrategy.FAIL_FAST 生效  暂时不支持二者同时设置
     * 优先级：注解exceptionClass>lockConfig exceptionClass>系统默认
     * 比如：com.ty.mid.framework.common.exception.FrameworkException
     * 注意：必须是RuntimeException的子类
     *
     * @see FailOnLockStrategy#THROWING
     * @see ReleaseExceptionStrategy#THROWING
     */
    String exceptionClass() default "";

    /**
     * 获取锁失败时，报错的错误信息
     * 仅当LockFailStrategy.FAIL_FAST或者ReleaseTimeoutStrategy.FAIL_FAST 生效  暂时不支持二者同时设置
     * 优先级：注解exceptionMsg>lockConfig exceptionMsg>系统默认
     *
     * @see FailOnLockStrategy#THROWING
     * @see ReleaseExceptionStrategy#THROWING
     */
    String exceptionMsg() default "";


    /**
     * 当前注解所属的类,用于多语义的lock注解区分具体注解类型,方便底层做定制逻辑处理
     */
    Class<?> annotationClass() default Lock.class;

}
