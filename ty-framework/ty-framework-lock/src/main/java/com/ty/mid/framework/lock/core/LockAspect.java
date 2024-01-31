package com.ty.mid.framework.lock.core;

import cn.hutool.core.map.MapUtil;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.core.aspect.AbstractAspect;
import com.ty.mid.framework.lock.adapter.LockAdapter;
import com.ty.mid.framework.lock.annotation.FailFastLock;
import com.ty.mid.framework.lock.annotation.LocalLock;
import com.ty.mid.framework.lock.annotation.Lock;
import com.ty.mid.framework.lock.factory.AdapterLockFactory;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.handler.LockInvocationException;
import com.ty.mid.framework.lock.manager.LockManagerKeeper;
import com.ty.mid.framework.lock.parser.FailFastLockParser;
import com.ty.mid.framework.lock.parser.LocalLockParser;
import com.ty.mid.framework.lock.registry.AbstractDecorateLockRegistry;
import com.ty.mid.framework.lock.registry.TypeLockRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
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

    private Lock convert2LockAnnotation(ProceedingJoinPoint joinPoint) {
        Method method = this.resolveMethod(joinPoint);
        log.debug("check api Lock from class: {}, method: {}", method.getDeclaringClass().getName(), method.getName());
        // get annotation
        Lock Lock = super.findAnnotation(method, Lock.class);
        if (Objects.nonNull(Lock)) {
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

    @Around("lockPointcutMain() || FailFastLockPointcut() || localLockPointcut() ")
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
        boolean lockResult = true;

        try {
            lockResult = this.acquireLock(lockObj, lockInfo);
        } catch (Exception e) {
            //注意:降级处理策略的返回值，会作为是否获取到锁的标志，允许无锁降级执行业务逻辑
            //如果注解自定义了获取锁失败的处理策略，则执行自定义的处理策略,注意:处理策略的返回值，会作为方法执行的返回值返回
            Object result = lockInfo.getLockExceptionStrategy().handle(lockInfo, lockObj, joinPoint, e);
            if (Objects.nonNull(result)) {
                return result;
            }
            //到这里，说明用户选择降级保障业务逻辑，这里进行无锁执行业务逻辑
            lockContext.downgrade = Boolean.TRUE;
        }

        //如果获取锁失败了，则进入失败的处理逻辑
        if (!lockResult) {
            log.debug("Timeout while acquiring Lock({})", lockInfo.getName());
            //如果注解自定义了获取锁失败的处理策略，则执行自定义的处理策略,注意:处理策略的返回值，会作为方法执行的返回值返回
            if (!StringUtils.isEmpty(lockInfo.getCustomLockFailStrategy())) {
                return handleCustomLockFail(lockInfo.getCustomLockFailStrategy(), joinPoint);

            }
            //如果注解级别没有定义处理策略，则使用全局的处理策略
            boolean handleResult = lockInfo.getLockFailStrategy().handle(lockInfo, lockObj, joinPoint);
            if (!handleResult) {
                return null;
            }

        }
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
     * 处理自定义加锁失败策略
     */
    private Object handleCustomLockFail(String lockFailCustermerHandler, JoinPoint joinPoint) throws Throwable {

        // prepare invocation context
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(lockFailCustermerHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        // invoke
        Object res;
        try {
            res = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new LockInvocationException("Fail to invoke custom lock timeout handler: " + lockFailCustermerHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        return res;
    }

    private LockAdapter getAdapter(LockInfo lockInfo) {
        LockFactory lockFactory = lockManagerKeeper.getLockFactory(lockInfo.getImplementer());
        if (lockFactory instanceof AdapterLockFactory) {
            AdapterLockFactory adapterLockFactory = (AdapterLockFactory) lockFactory;
            LockAdapter adapter = adapterLockFactory.getAdapter();
            if (Objects.nonNull(adapter)) {
                return adapter;
            }
        }
        return null;
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
     * 加锁,优先使用lock的适配器定义的加锁方法，如果没有定义适配器，则默认使用java lock的tryLock方法
     * 注意：默认情况下，由于java的tryLock没有leaseTime（持有锁有效期）参数，此时设置的leaseTime参数将无效
     */
    private boolean acquireLock(java.util.concurrent.locks.Lock lock, LockInfo lockInfo) throws Throwable {
        LockAdapter adapter = this.getAdapter(lockInfo);
        if (Objects.isNull(adapter)) {
            //没有定义适配器的，默认使用java定义tryLock方法，此时：leaseTime参数将无效
            return lock.tryLock(lockInfo.getWaitTime(), lockInfo.getTimeUnit());
        }
        return adapter.acquire(lock, lockInfo);
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
            try {
                LockAdapter adapter = this.getAdapter(lockContext.lockInfo);
                java.util.concurrent.locks.Lock nowLock = lockContext.getLock();
                if (Objects.isNull(adapter)) {
                    //没有定义适配器的，默认使用java定义tryLock方法，此时：leaseTime参数将无效
                    nowLock.unlock();
                } else {
                    boolean release = adapter.release(nowLock, lockContext.getLockInfo());
                    if (!release) {
                        handleReleaseTimeout(lockContext.getLockInfo(), joinPoint);
                    }
                }

            } catch (Exception e) {
                log.warn("exception when unlock，e:", e);

                if (lockContext.getDowngrade().equals(Boolean.TRUE)) {
                    //如果为降级逻辑，则释放锁的异常处理逻辑失效
                    return;
                }
                // avoid release lock twice when exception happens below
                lockContext.setRes(false);
                handleReleaseTimeout(lockContext.getLockInfo(), joinPoint);
            }
            // avoid release lock twice when exception happens below
            lockContext.setRes(false);
            log.debug("lock release error,lockContext:{}", lockContext);
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
     * 处理释放锁时已超时
     */
    private void handleReleaseTimeout(LockInfo lockInfo, JoinPoint joinPoint) throws Throwable {

        if (log.isWarnEnabled()) {
            log.warn("Timeout while release Lock({})", lockInfo.getName());
        }
        if (!StringUtils.isEmpty(lockInfo.getCustomReleaseTimeoutStrategy())) {
            handleCustomReleaseTimeout(lockInfo.getCustomReleaseTimeoutStrategy(), joinPoint);
            return;
        }
        lockInfo.getReleaseTimeoutStrategy().handle(lockInfo);
    }

    /**
     * 处理自定义释放锁时已超时
     */
    private void handleCustomReleaseTimeout(String releaseTimeoutHandler, JoinPoint joinPoint) throws Throwable {

        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(releaseTimeoutHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customReleaseTimeoutStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        try {
            handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new LockInvocationException("Fail to invoke custom release timeout handler: " + releaseTimeoutHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
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
