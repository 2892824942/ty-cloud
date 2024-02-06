package com.ty.mid.framework.lock.core;

import cn.hutool.core.map.MapUtil;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.core.aspect.AbstractAspect;
import com.ty.mid.framework.lock.annotation.AntiReLock;
import com.ty.mid.framework.lock.annotation.FailFastLock;
import com.ty.mid.framework.lock.annotation.LocalLock;
import com.ty.mid.framework.lock.annotation.Lock;
import com.ty.mid.framework.lock.manager.LockManagerKeeper;
import com.ty.mid.framework.lock.parser.AntiReLockParser;
import com.ty.mid.framework.lock.parser.FailFastLockParser;
import com.ty.mid.framework.lock.parser.LocalLockParser;
import com.ty.mid.framework.lock.registry.AbstractDecorateLockRegistry;
import com.ty.mid.framework.lock.registry.TypeLockRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by suyouliang on 2022/03/26
 * Content :给添加@Lock切面加锁处理
 */
@Aspect
@Component
@Order(0)
@Slf4j
public class LockAspect extends AbstractAspect {
    /**
     * 不可以使用HashMap或ConcurrentHashMap，存在无锁降级执行业务逻辑的可能，并发则可能出现LockRes被覆盖
     * LockRes由lockInfo产生，lockInfo的数据来源于lockConfig及Lock注解（这个不太可能实时刷新）
     * 触发场景：当系统支持配置实时刷新时，lockConfig配置更改会导致前后的lock可能不是同一个，如果使用的是HashMap或ConcurrentHashMap，存在多线程覆盖，导致lock被替换
     * 使用TreadLocal，支持线程内 存在多个lock。每个lock执行完成后将清理lock上下文的lockRes，执行下一个lock时，lock的上下文是干净的。
     */
    private static final ThreadLocal<Map<String, LockContext>> CURRENT_THREAD_LOCAL_LOCKRES_MAP = new ThreadLocal<>();
    @Resource
    LockManagerKeeper lockManagerKeeper;
    @Resource
    private LockInfoProvider lockInfoProvider;

    public static LockContext getLockContext(String lockName) {
        Map<String, LockContext> lockContextMap = CURRENT_THREAD_LOCAL_LOCKRES_MAP.get();
        if (MapUtil.isEmpty(lockContextMap)) {
            return null;
        }
        return lockContextMap.get(getCurrentLockId(lockName));
    }

    /**
     * 获取当前锁在map中的key
     *
     * @param lockName
     * @return
     */
    private static String getCurrentLockId(String lockName) {
        Validator.requireNonEmpty(lockName, "lockName can not be empty");
        return Thread.currentThread().getId() + ":" + lockName;
    }

    @Pointcut("@annotation(com.ty.mid.framework.lock.annotation.Lock)")
    public void lockPointcutMain() {
    }

    @Pointcut("@annotation(com.ty.mid.framework.lock.annotation.FailFastLock)")
    public void FailFastLockPointcut() {
    }

    @Pointcut("@annotation(com.ty.mid.framework.lock.annotation.LocalLock)")
    public void localLockPointcut() {
    }

    @Pointcut("@annotation(com.ty.mid.framework.lock.annotation.AntiReLock)")
    public void antiReLockPointcut() {
    }


    private Lock convert2LockAnnotation(ProceedingJoinPoint joinPoint) {
        Method method = this.resolveMethod(joinPoint);
        log.debug("check api Lock from class: {}, method: {}", method.getDeclaringClass().getName(), method.getName());
        // get annotation
        Lock Lock = super.findAnnotation(method, Lock.class);
        if (Objects.nonNull(Lock)) {
            AntiReLock antiReLock = super.findAnnotation(method, AntiReLock.class);
            if (Objects.nonNull(antiReLock)) {
                return AntiReLockParser.getInstance().convert(antiReLock, joinPoint);
            }
            FailFastLock failFastLock = super.findAnnotation(method, FailFastLock.class);
            if (Objects.nonNull(failFastLock)) {
                return FailFastLockParser.getInstance().convert(failFastLock, joinPoint);
            }
            LocalLock localLock = super.findAnnotation(method, LocalLock.class);
            if (Objects.nonNull(localLock)) {
                return LocalLockParser.getInstance().convert(localLock, joinPoint);
            }
            return Lock;
        }
        throw new FrameworkException("lock parse error");

    }

    @Around("lockPointcutMain() || antiReLockPointcut() || FailFastLockPointcut() || localLockPointcut() ")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("lock aspect excuse");
        Lock annoLock = this.convert2LockAnnotation(joinPoint);
        LockInfo lockInfo = lockInfoProvider.get(joinPoint, annoLock);
        Map<String, LockContext> lockResMap = CURRENT_THREAD_LOCAL_LOCKRES_MAP.get();
        if (MapUtil.isEmpty(lockResMap)) {
            lockResMap = new HashMap<>();
            CURRENT_THREAD_LOCAL_LOCKRES_MAP.set(lockResMap);
        }
        String currentLock = getCurrentLockId(lockInfo.getName());
        LockContext lockContext = lockResMap.get(currentLock);
        if (Objects.isNull(lockContext)) {
            lockContext = new LockContext(lockInfo, joinPoint, false);
            lockResMap.putIfAbsent(currentLock, lockContext);
        }
        java.util.concurrent.locks.Lock lockObj = Objects.isNull(lockContext.getLock()) ? this.getLock(lockInfo) : lockContext.getLock();

        lockObj.tryLock(lockInfo.getWaitTime(), lockInfo.getTimeUnit());

        lockContext.setLock(lockObj);
        lockContext.setRes(true);

        log.debug("success get lock ,lockInfo:{}", lockInfo);


        return joinPoint.proceed();
    }

    @AfterReturning(pointcut = "lockPointcutMain() || FailFastLockPointcut() || localLockPointcut() ")
    public void afterReturning(JoinPoint joinPoint) throws Throwable {
        Lock annoLock = this.convert2LockAnnotation((ProceedingJoinPoint) joinPoint);
        String currentLock = this.getCurrentLockId(joinPoint, annoLock);
        releaseLock(joinPoint, currentLock);
        cleanUpThreadLocal(currentLock);
    }

    @AfterThrowing(pointcut = "lockPointcutMain() || FailFastLockPointcut() || localLockPointcut() ", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) throws Throwable {
        Lock annoLock = this.convert2LockAnnotation((ProceedingJoinPoint) joinPoint);
        String currentLock = this.getCurrentLockId(joinPoint, annoLock);
        releaseLock(joinPoint, currentLock);
        cleanUpThreadLocal(currentLock);
        throw ex;
    }

    /**
     * 1.获取锁，如果定义的LockRegistry 不是TypeLockRegistry的子类，则锁的类型lockType字段将失效
     * 2.如想使用自定义锁类型，需注册LockRegistry的实例为TypeLockRegistry的子类
     * 3.如果注解中没有指定 厂商，是否支持事务，是否支持本地缓存，则根据全局设置lockConfig确定
     */
    private java.util.concurrent.locks.Lock getLock(LockInfo lockInfo) {

        LockRegistry lockRegistry = lockManagerKeeper.getLockRegistry(lockInfo.getImplementer());
        //如果是装饰的lockRegistry,直接调用doGetLock
        if (lockRegistry instanceof AbstractDecorateLockRegistry) {
            AbstractDecorateLockRegistry decorateLockRegistry = (AbstractDecorateLockRegistry) lockRegistry;
            return decorateLockRegistry.doGetLock(lockInfo);
        }
        //如果是TypeLockRegistry,则调用obtain type方法,此时:基于包装的能力只受全局控制,注解相关的配置(缓存及事务)失效
        if (lockRegistry instanceof TypeLockRegistry) {
            TypeLockRegistry typeLockRegistry = (TypeLockRegistry) lockRegistry;
            return typeLockRegistry.obtain(lockInfo.getType().getCode(), lockInfo.getName());
        }
        //没有定义适配器的,调用spring的原生定义，此时：leaseTime参数将无效
        return lockRegistry.obtain(lockInfo.getName());
    }


    /**
     * 释放锁
     */
    private void releaseLock(JoinPoint joinPoint, String currentLock) throws Throwable {
        LockContext lockContext = CURRENT_THREAD_LOCAL_LOCKRES_MAP.get().get(currentLock);
        if (Objects.isNull(lockContext)) {
            throw new NullPointerException("Please check whether the input parameter used as the lock key value has been modified in the method," +
                    " which will cause the acquire and release locks to have different key values and throw npe current LockKey:" + currentLock);
        }

        if (lockContext.getRes()) {
            java.util.concurrent.locks.Lock nowLock = lockContext.getLock();
            nowLock.unlock();
        }
        log.debug("lock release successful,lockContext:{}", lockContext);
    }

    // 支持api级锁的可重入，线程上下文所有的数据为空后，执行线程上下文删除
    private void cleanUpThreadLocal(String currentLock) {
        Map<String, LockContext> lockResMap = CURRENT_THREAD_LOCAL_LOCKRES_MAP.get();
        try {
            lockResMap = CURRENT_THREAD_LOCAL_LOCKRES_MAP.get();
            lockResMap.remove(currentLock);
        } finally {
            if (lockResMap.isEmpty()) {
                CURRENT_THREAD_LOCAL_LOCKRES_MAP.remove();
            }

        }


    }

    /**
     * 获取当前锁在map中的key
     *
     * @param joinPoint
     * @param lock
     * @return
     */
    private String getCurrentLockId(JoinPoint joinPoint, Lock lock) {
        return getCurrentLockId(lockInfoProvider.getLockName(joinPoint, lock));
    }

    /**
     * lock上下文
     */
    @Data
    public static class LockContext {
        private ProceedingJoinPoint joinPoint;
        private LockInfo lockInfo;
        private java.util.concurrent.locks.Lock lock;
        private Boolean res;
        private Boolean downgrade = Boolean.FALSE;

        LockContext(LockInfo lockInfo, ProceedingJoinPoint joinPoint, Boolean res) {
            this.lockInfo = lockInfo;
            this.joinPoint = joinPoint;
            this.res = res;
        }
    }


}
