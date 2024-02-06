package com.ty.mid.framework.lock.annotation;


import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.core.LockInfoProvider;
import com.ty.mid.framework.lock.strategy.FailOnLockStrategy;
import com.ty.mid.framework.lock.strategy.ReleaseExceptionStrategy;
import org.aspectj.lang.JoinPoint;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author suyouliang
 * @date 2022/03/26
 * 防重锁,用于业务防重使用,默认和FailFastLock一样
 * 特别的:
 * 1.支持设置等待时间
 * 2.支持设置message,并支持全局的AntiReLockMsg
 * AntiReLock是为了更好的语义以及针对web层防重这里类业务做的注解,和@FailFastLock在Msg以及防重方面大部分相同,
 * 但是又存在业务自定义的内容,比如msg可能不一致(需要更好的用户体验msg),部分业务可能需要有容忍的防重锁(waitTime>0)等
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Lock
@Documented
public @interface AntiReLock {
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
    long waitTime() default 0L;


    /**
     * 时间单位，此参数作用：waitTime及leaseTime
     *
     * @return leaseTime
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;


    /**
     * 获取锁失败时，报错的错误信息
     * 仅当LockFailStrategy.FAIL_FAST或者ReleaseTimeoutStrategy.FAIL_FAST 生效  暂时不支持二者同时设置
     * 对于基础实现Lock的优先级：注解exceptionMsg>lockConfig exceptionMsg>系统默认
     * 本类的message优先级:注解exceptionMsg>LockConfig.antiReLockMsg>lockConfig.exceptionMsg>系统默认
     * <p>
     * 关于优先级
     *
     * @see LockInfoProvider#get(JoinPoint, Lock)
     * <p>
     * 关于系统默认msg
     * @see FailOnLockStrategy#THROWING
     * @see ReleaseExceptionStrategy#THROWING
     */
    String exceptionMsg() default "";

}
