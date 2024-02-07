package com.ty.mid.framework.lock.config;

import com.ty.mid.framework.core.config.AbstractConfig;
import com.ty.mid.framework.lock.annotation.Lock;
import com.ty.mid.framework.lock.core.LockInfoProvider;
import com.ty.mid.framework.lock.enums.LockImplementer;
import com.ty.mid.framework.lock.strategy.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * Created by suyouliang on 2020/03/21.
 */
@ConfigurationProperties(prefix = LockConfig.PREFIX)
@Data
public class LockConfig extends AbstractConfig {

    public static final String PREFIX = FRAMEWORK_PREFIX + "lock";
    /**
     * 默认的时间单位:秒
     */
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    /**
     * 加锁失败的处理策略
     *
     * @return lockTimeoutStrategy
     */
    private FailOnLockStrategy lockFailStrategy = FailOnLockStrategy.THROWING;
    /**
     * 加锁异常的处理策略
     *
     * @return lockTimeoutStrategy
     */
    private ExceptionOnLockStrategy exceptionOnLockStrategy = ExceptionOnLockStrategy.THROWING;
    /**
     * 释放锁时异常的处理策略
     *
     * @return releaseTimeoutStrategy
     */
    private ReleaseExceptionStrategy releaseExceptionStrategy = ReleaseExceptionStrategy.THROWING;
    /**
     * 是否开启
     */
    private boolean enable = Boolean.FALSE;
    /**
     * lock厂商实现类型
     */
    private LockImplementer implementer = LockImplementer.REDIS;

    /**
     * 是否支持本地lock二级缓存
     */
    private boolean withLocalCache = Boolean.FALSE;
    /**
     * 锁在事务上下文中的处理策略
     */
    private LockTransactionStrategy transactionStrategy = LockTransactionStrategy.DISABLED;

    /**
     * 死锁处理策略(不建议生产开启)
     */
    private CycleLockStrategy cycleLockStrategy = CycleLockStrategy.DISABLED;
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
     * <p>
     * 关于系统默认msg
     *
     * @see FailOnLockStrategy#THROWING
     * @see ReleaseExceptionStrategy#THROWING
     */
    private Class<? extends RuntimeException> exceptionClass;
    /**
     * 获取锁失败时，报错的错误信息
     * 仅当LockFailStrategy.FAIL_FAST或者ReleaseTimeoutStrategy.FAIL_FAST 生效  此处设置二者都会生效
     * 优先级：注解exceptionClass>lockConfig.exceptionClass>系统默认
     * <p>
     * 关于系统默认msg
     *
     * @see FailOnLockStrategy#THROWING
     * @see ReleaseExceptionStrategy#THROWING
     */
    private String exceptionMsg;

    //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓其他相关配置↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    /**
     * 此配置仅对@AntiReLock生效
     * <p>
     * AntiReLock是为了更好的语义以及针对web层防重这一类业务做的注解,可和@FailFastLock在Msg以及防重方面大部分相同,
     * 但是又存在业务自定义的内容,比如msg可能不一致(需要更好的用户体验msg),部分业务可能需要有容忍的防重锁(waitTime>0)等
     * <p>
     * 仅当LockFailStrategy.FAIL_FAST或者ReleaseTimeoutStrategy.FAIL_FAST 生效  暂时不支持二者同时设置
     * 对于基础实现Lock的优先级：注解exceptionMsg>lockConfig.exceptionMsg>系统默认
     * 本类的message优先级:注解exceptionMsg>LockConfig.antiReLockMsg>lockConfig.exceptionMsg>系统默认
     * <p>
     * 关于优先级
     *
     * @see LockInfoProvider#getExceptionMsg(Lock, LockConfig)
     * <p>
     * 关于系统默认msg
     * @see FailOnLockStrategy#THROWING
     * @see ReleaseExceptionStrategy#THROWING
     */
    private String antiReLockMsg = "";

    private String lockNamePrefix = "lock";

    private String lockNameSeparator = ":";


}
