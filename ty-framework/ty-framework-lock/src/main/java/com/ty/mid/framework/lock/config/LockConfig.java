package com.ty.mid.framework.lock.config;

import com.ty.mid.framework.lock.strategy.ExceptionOnLockStrategy;
import com.ty.mid.framework.lock.strategy.FailOnLockStrategy;
import com.ty.mid.framework.lock.strategy.LockTransactionStrategy;
import com.ty.mid.framework.lock.strategy.ReleaseTimeoutStrategy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * Created by suyouliang on 2020/03/21.
 */
@ConfigurationProperties(prefix = LockConfig.PREFIX)
@Data
public class LockConfig {

    public static final String PREFIX = "application.lock";
    /**
     * 默认的时间单位:秒
     */
    TimeUnit timeUnit = TimeUnit.SECONDS;
    /**
     * 加锁失败的处理策略
     *
     * @return lockTimeoutStrategy
     */
    FailOnLockStrategy lockFailStrategy = FailOnLockStrategy.FAIL_FAST;
    /**
     * 加锁异常的处理策略
     *
     * @return lockTimeoutStrategy
     */
    ExceptionOnLockStrategy exceptionOnLockStrategy = ExceptionOnLockStrategy.THROW_EXCEPTION;
    /**
     * 释放锁时已超时的处理策略
     *
     * @return releaseTimeoutStrategy
     */
    ReleaseTimeoutStrategy releaseTimeoutStrategy = ReleaseTimeoutStrategy.FAIL_FAST;
    /**
     * 是否开启
     */
    private boolean enable = Boolean.FALSE;
    /**
     * lock厂商实现类型
     */
    private LockImplementer implementer = LockImplementer.REDIS;
    /**
     * 是否支持上下文感知
     */
    private boolean supportTransaction = Boolean.TRUE;
    /**
     * 是否支持本地lock二级缓存
     */
    private boolean withLocalCache = Boolean.FALSE;
    /**
     * 仅开启supportTransaction生效
     */
    private LockTransactionStrategy transactionStrategy = LockTransactionStrategy.WARMING;
    /**
     * 默认的等待时间
     */
    private long waitTime = 2;
    /**
     * 默认的等待时间单位
     */
    private long leaseTime = 60;
    /**
     * 获取锁失败时，报错的异常类型
     * 仅当LockFailStrategy.FAIL_FAST或者ReleaseTimeoutStrategy.FAIL_FAST 生效 此处设置二者都会生效
     * 优先级：注解exceptionClass>lockConfig exceptionClass>系统默认
     * 比如：com.ty.mid.framework.common.exception.FrameworkException
     * 注意：必须是RuntimeException的子类
     *
     * @see FailOnLockStrategy#FAIL_FAST
     * @see ReleaseTimeoutStrategy#FAIL_FAST
     */
    private Class<? extends RuntimeException> exceptionClass;
    /**
     * 获取锁失败时，报错的错误信息
     * 仅当LockFailStrategy.FAIL_FAST或者ReleaseTimeoutStrategy.FAIL_FAST 生效  此处设置二者都会生效
     * 优先级：注解exceptionClass>lockConfig exceptionClass>系统默认
     *
     * @see FailOnLockStrategy#FAIL_FAST
     * @see ReleaseTimeoutStrategy#FAIL_FAST
     */
    private String exceptionMsg;

    /**
     * lock实现厂商类型
     */
    public enum LockImplementer {
        /**
         * 系统区分默认值使用，开发者请勿使用
         */
        EMPTY,
        /**
         * 本地JVM实现
         */
        JVM,
        /**
         * redis实现
         */
        REDIS,
        /**
         * zookeeper实现
         */
        ZOOKEEPER,
        /**
         * etcd实现
         */
        ETCD,
        /**
         * mysql实现
         */
        MYSQL,
        ;
    }

}
