package com.ty.mid.framework.lock.model;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.enums.LockType;
import com.ty.mid.framework.lock.registry.TypeLockRegistry;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * Created by 苏友良 on 2022/03/26.
 * Content :锁基本信息
 */
@Data
public class LockInfo {

    /**
     * 加锁失败的处理策略
     */
    FailOnLockStrategy lockFailStrategy;
    /**
     * 加锁异常的处理策略
     * 注意：定义的方法参数需要和注解所在的方法参数保持一致
     * 此方式与lockFailStrategy中的CUSTOM不同点在于：此方式可对方法具体参数做定制化的处理策略，后者更适合做全局的默认处理
     * 优先级：customLockFailStrategy>lockFailStrategy>lockConfig
     *
     * @return String
     */
    String customLockFailStrategy;
    /**
     * 加锁异常的处理策略
     */
    ExceptionOnLockStrategy lockExceptionStrategy;
    /**
     * 释放锁时已超时的处理策略
     */
    ReleaseTimeoutStrategy releaseTimeoutStrategy;
    /**
     * 自定义释放锁时已超时的处理策略
     * 注意：定义的方法参数需要和注解所在的方法参数保持一致
     * 此方式与releaseTimeoutStrategy中的CUSTOM不同点在于：此方式可对方法具体参数做定制化的处理策略，后者更适合做全局的默认处理
     * 优先级：customReleaseTimeoutStrategy>releaseTimeoutStrategy>lockConfig
     */
    String customReleaseTimeoutStrategy;
    /**
     * lock厂商实现类型
     */
    private LockConfig.LockImplementer implementer;
    /**
     * 是否支持上下文感知
     */
    private Boolean supportTransaction;
    /**
     * 是否支持本地lock二级缓存
     */
    private Boolean withLocalCache;
    /**
     * lock 的类型
     * 注意：如果全局注入的LockRegistry 不是此框架TypeLockRegistry的子类，则此字段失效
     *
     * @see TypeLockRegistry
     */
    private LockType type;
    private String name;
    private long waitTime;
    /**
     * 存在定义的有效时间实际比方法执行时间短的情况（比如开发人员误设置，或者网络抖动导致执行时间过长），此时将会导致锁的独占逻辑失效
     * 存在厂商已经提供逻辑解决以上问题，比如redisson的看门狗，本框架所有实现基于Spring的 LockRegistry定义，默认此参数不生效
     * 但为了更好的扩展性，此参数会暴露给开发者并一直存在于lock的上下文中，允许开发者自定义的lockRegistry使用此参数实现某些自定义逻辑
     */
    private long leaseTime;
    private TimeUnit timeUnit;
    /**
     * 当快速失败时，报错的异常类型  支持 lockFailStrategy.FAIL_FAST 及releaseTimeoutStrategy.FAIL_FAST 暂不支持同时定义二者
     */
    private Class<? extends RuntimeException> exceptionClass;
    /**
     * 获取锁失败时，报错的错误信息
     */
    private String exceptionMsg;

    public LockInfo() {
    }


    @Override
    public String toString() {
        return "LockInfo{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", waitTime=" + waitTime +
                ", leaseTime=" + leaseTime +
                ", timeUnit=" + timeUnit +
                '}';
    }
}
