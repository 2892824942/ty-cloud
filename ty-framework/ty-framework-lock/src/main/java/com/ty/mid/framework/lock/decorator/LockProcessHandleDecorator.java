package com.ty.mid.framework.lock.decorator;

import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.exception.LockInvocationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * lock过程处理装饰者
 * 作用:
 * 在上锁解锁过程中出现特定问题,如获取锁失败,释放锁异常等场景,执行配置定义的的对应策略
 */
@Slf4j
public class LockProcessHandleDecorator extends AbstractLockDecorator {
    public LockProcessHandleDecorator(Lock distributedLock, LockInfo lockInfo) {
        super(distributedLock, lockInfo);
    }

    @Override
    public void lock() {
        try {
            super.realLock.lock();
            printLog(1, true);
        } catch (Exception e) {
            //如果注解自定义了获取锁异常的处理策略，则执行自定义的处理策略
            lockInfo.getLockExceptionStrategy().handle(lockInfo, null, null, e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            super.realLock.lockInterruptibly();
            printLog(1, true);
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            //如果注解自定义了获取锁异常的处理策略，则执行自定义的处理策略
            lockInfo.getLockExceptionStrategy().handle(lockInfo, null, null, e);
        }

    }

    @Override
    public boolean tryLock() {
        try {
            boolean result = super.realLock.tryLock();
            if (!result) {
                lockInfo.getLockFailStrategy().handle(lockInfo, realLock, null);
            }
            printLog(1, result);
            return result;
        } catch (Exception e) {
            //如果注解自定义了获取锁异常的处理策略，则执行自定义的处理策略
            lockInfo.getLockExceptionStrategy().handle(lockInfo, null, null, e);
        }
        return false;
    }

    /**
     * tryLock
     *
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        try {
            boolean result = super.realLock.tryLock(0, TimeUnit.MILLISECONDS);
            if (!result) {
                lockInfo.getLockFailStrategy().handle(lockInfo, realLock, null);
            }
            printLog(1, result);
            return result;
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            //如果注解自定义了获取锁异常的处理策略，则执行自定义的处理策略
            lockInfo.getLockExceptionStrategy().handle(lockInfo, realLock, null, e);
            return false;
        }
    }


    @SneakyThrows
    @Override
    public void unlock() {
        try {
            super.realLock.unlock();
            printLog(2, true);
        } catch (Exception e) {
            log.warn("Exception while release Lock({})", lockInfo.getName());

            if (!StringUtils.isEmpty(lockInfo.getCustomReleaseExceptionStrategy())) {
                handleCustomReleaseException(lockInfo.getCustomReleaseExceptionStrategy(), lockInfo.getJoinPoint());
                return;
            }
            lockInfo.getReleaseExceptionStrategy().handle(lockInfo, realLock, e);
        }
    }


    /**
     * 处理自定义释放锁时异常
     * TODO 更改为SpringEL
     */
    private void handleCustomReleaseException(String releaseExceptionHandler, JoinPoint joinPoint) throws Throwable {

        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(releaseExceptionHandler, currentMethod.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customReleaseExceptionStrategy", e);
        }
        Object[] args = joinPoint.getArgs();

        try {
            handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new LockInvocationException("Fail to invoke customReleaseExceptionHandler: " + releaseExceptionHandler, e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }


    private void printLog(int type, boolean result) {
        if (log.isDebugEnabled()) {
            if (type == 1) {
                if (result) {
                    log.debug("3.lock successful,lockKey:{}", lockInfo.getName());
                } else {
                    log.debug("3.lock fail,lockKey:{}", lockInfo.getName());
                }

            } else {
                log.debug("4.unlock successful,lockKey:{}", lockInfo.getName());
            }
        }
    }

}