package com.ty.mid.framework.lock.core;

import com.ty.mid.framework.common.constant.BooleanEnum;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.lock.annotation.Lock;
import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.strategy.ExceptionOnLockStrategy;
import com.ty.mid.framework.lock.strategy.FailOnLockStrategy;
import com.ty.mid.framework.lock.strategy.ReleaseTimeoutStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * Created by suyouliang on 2022/03/26.
 */
@Slf4j
public class LockInfoProvider {

    private static final String LOCK_NAME_PREFIX = "lock";
    private static final String LOCK_NAME_SEPARATOR = ":";
    private static final Logger logger = LoggerFactory.getLogger(LockInfoProvider.class);
    @Autowired
    private LockConfig lockConfig;
    @Autowired
    private BusinessKeyProvider businessKeyProvider;

    @SuppressWarnings("unchecked")
    LockInfo get(JoinPoint joinPoint, Lock lock) {
        //锁的名字，锁的粒度就是这里控制的
        String lockName = getLockName(joinPoint, lock);
        long waitTime = getWaitTime(lock);
        long leaseTime = getLeaseTime(lock);
        //如果占用锁的时间设计不合理，则打印相应的警告提示
        if (leaseTime == -1 && log.isWarnEnabled()) {
            log.warn("Trying to acquire Lock({}) with no expiration, " +
                    "lock will keep prolong the lock expiration while the lock is still holding by current thread. " +
                    "This may cause dead lock in some circumstances.", lockName);
        }
        //lock厂商实现类型
        LockConfig.LockImplementer implementer = getValueOrDefault(LockConfig.LockImplementer.EMPTY, lock.implementer(), lockConfig.getImplementer());
        //是否支持上下文感知

        BooleanEnum supportTransactionBoolean = getValueOrDefault(BooleanEnum.NULL, lock.supportTransaction(), BooleanEnum.booleanOf(lockConfig.isSupportTransaction()));
        boolean supportTransaction = supportTransactionBoolean.getValue();
        //是否支持本地lock二级缓存
        BooleanEnum withLocalCacheBoolean = getValueOrDefault(BooleanEnum.NULL, lock.withLocalCache(), BooleanEnum.booleanOf(lockConfig.isWithLocalCache()));
        Boolean withLocalCache = withLocalCacheBoolean.getValue();

        //加锁失败的处理策略
        FailOnLockStrategy lockFailStrategy = getValueOrDefault(FailOnLockStrategy.EMPTY, lock.lockFailStrategy(), lockConfig.getLockFailStrategy());
        //加锁异常的处理策略
        ExceptionOnLockStrategy lockExceptionStrategy = getValueOrDefault(ExceptionOnLockStrategy.EMPTY, lock.lockExceptionStrategy(), lockConfig.getExceptionOnLockStrategy());
        //释放锁时已超时的处理策略
        ReleaseTimeoutStrategy releaseTimeoutStrategy = getValueOrDefault(ReleaseTimeoutStrategy.EMPTY, lock.releaseTimeoutStrategy(), lockConfig.getReleaseTimeoutStrategy());

        Class<? extends RuntimeException> exceptionClass = this.getExceptionClass(lock, lockConfig);
        String exceptionMsg = StringUtils.isEmpty(lock.exceptionMsg()) ? lockConfig.getExceptionMsg() : lock.exceptionMsg();

        LockInfo lockInfo = new LockInfo();
        lockInfo.setImplementer(implementer);
        lockInfo.setName(lockName);
        lockInfo.setWaitTime(waitTime);
        lockInfo.setLeaseTime(leaseTime);
        lockInfo.setTimeUnit(lock.timeUnit());
        lockInfo.setSupportTransaction(supportTransaction);
        lockInfo.setWithLocalCache(withLocalCache);
        lockInfo.setType(lock.lockType());

        lockInfo.setLockFailStrategy(lockFailStrategy);
        lockInfo.setCustomLockFailStrategy(lock.customLockFailStrategy());

        lockInfo.setLockExceptionStrategy(lockExceptionStrategy);

        lockInfo.setReleaseTimeoutStrategy(releaseTimeoutStrategy);
        lockInfo.setCustomReleaseTimeoutStrategy(lock.customReleaseTimeoutStrategy());
        lockInfo.setLockTransactionStrategy(lockConfig.getTransactionStrategy());

        lockInfo.setExceptionClass(exceptionClass);
        lockInfo.setExceptionMsg(exceptionMsg);
        return lockInfo;
    }

    public LockInfo transform2(LockConfig lockConfig, String type, String lockKey) {
        //锁的名字，锁的粒度就是这里控制的
        String lockName = doGetLockName(lockKey);
        long leaseTime = lockConfig.getLeaseTime();
        //如果占用锁的时间设计不合理，则打印相应的警告提示
        if (leaseTime == -1 && log.isWarnEnabled()) {
            log.warn("Trying to acquire Lock({}) with no expiration, " +
                    "lock will keep prolong the lock expiration while the lock is still holding by current thread. " +
                    "This may cause dead lock in some circumstances.", lockName);
        }

        LockInfo lockInfo = new LockInfo();
        lockInfo.setImplementer(lockConfig.getImplementer());
        lockInfo.setName(lockName);
        lockInfo.setWaitTime(lockConfig.getWaitTime());
        lockInfo.setLeaseTime(leaseTime);
        lockInfo.setTimeUnit(lockConfig.getTimeUnit());
        lockInfo.setSupportTransaction(lockConfig.isSupportTransaction());
        lockInfo.setWithLocalCache(lockConfig.isWithLocalCache());
        lockInfo.setType(null);

        lockInfo.setLockFailStrategy(lockConfig.getLockFailStrategy());
        lockInfo.setCustomLockFailStrategy("");

        lockInfo.setLockExceptionStrategy(lockConfig.getExceptionOnLockStrategy());

        lockInfo.setReleaseTimeoutStrategy(lockConfig.getReleaseTimeoutStrategy());
        lockInfo.setCustomReleaseTimeoutStrategy("");
        lockInfo.setLockTransactionStrategy(lockConfig.getTransactionStrategy());

        lockInfo.setExceptionClass(lockConfig.getExceptionClass());
        lockInfo.setExceptionMsg(lockConfig.getExceptionMsg());
        return lockInfo;
    }

    private <T> T getValueOrDefault(T emptyValue, T nowValue, T ifEmptyValue) {
        if (Objects.isNull(nowValue) || emptyValue == nowValue) {
            return ifEmptyValue;
        }
        if (nowValue instanceof String) {
            if (StringUtils.isEmpty((String) nowValue)) {
                return ifEmptyValue;
            }
        }
        return nowValue;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends RuntimeException> getExceptionClass(Lock lock, LockConfig lockConfig) {
        if (StringUtils.isEmpty(lock.exceptionClass())) {
            return Objects.isNull(lockConfig.getExceptionClass()) ? RuntimeException.class : lockConfig.getExceptionClass();
        }
        try {
            return (Class<? extends RuntimeException>) Class.forName(lock.exceptionClass());
        } catch (ClassNotFoundException e) {
            log.error("no class find for {}", lock.exceptionClass());
            throw new FrameworkException(e);
        } catch (ClassCastException e) {
            log.error("exceptionClass must be a subclass of RuntimeException，error config exceptionClass:{}", lock.exceptionClass());
            throw new FrameworkException(e);
        }
    }

    String getLockName(JoinPoint joinPoint, Lock lock) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String businessKeyName = businessKeyProvider.getKeyName(joinPoint, lock);
        //锁的名字，锁的粒度就是这里控制的
        return doGetLockName(getNameWhenEmpty(businessKeyName, signature));
    }

    private String doGetLockName(String handledLockKey) {
        return LOCK_NAME_PREFIX + LOCK_NAME_SEPARATOR + handledLockKey;
    }

    /**
     * 获取锁的name，如果没有指定，则按全类名拼接方法名处理
     *
     * @param annotationName
     * @param signature
     * @return
     */
    private String getNameWhenEmpty(String annotationName, MethodSignature signature) {
        if (annotationName.isEmpty()) {
            return String.format("%s.%s", signature.getDeclaringTypeName(), signature.getMethod().getName());
        } else {
            return annotationName;
        }
    }


    private long getWaitTime(Lock lock) {
        return lock.waitTime() == Long.MIN_VALUE ?
                lockConfig.getWaitTime() : lock.waitTime();
    }

    private long getLeaseTime(Lock lock) {
        return lock.leaseTime() == Long.MIN_VALUE ?
                lockConfig.getLeaseTime() : lock.leaseTime();
    }
}
