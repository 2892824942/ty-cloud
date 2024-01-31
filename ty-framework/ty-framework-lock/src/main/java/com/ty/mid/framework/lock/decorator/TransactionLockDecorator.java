package com.ty.mid.framework.lock.decorator;

import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.lock.core.LockAspect;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.handler.LockTransactionForbiddenException;
import com.ty.mid.framework.lock.strategy.LockTransactionStrategy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
public class TransactionLockDecorator extends AbstractLockDecorator {


    public TransactionLockDecorator(Lock distributedLock, LockInfo lockInfo) {
        super(distributedLock, lockInfo);
    }

    @Override
    public void lock() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            distributedLock.lock();
            return;
        }
        //当前存在事务上下文
        if (isAbsoluteTransaction()) {
            //当前事务上下文为独立的事务
            distributedLock.lock();
            return;
        }
        LockTransactionStrategy transactionStrategy = super.lockInfo.getLockTransactionStrategy();
        switch (transactionStrategy) {
            case THREAD_SAFE:
            case WARMING:
                log.warn("use lock in a transaction context may cause lock invalidation,you can use the lock transaction strategy:THREAD_SAFE or move your lock outside of a transaction context");
            case FORBIDDEN:
                throw new LockTransactionForbiddenException("use lock in a transaction context is forbidden in system config.");
            default:
                distributedLock.lock();
        }


    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        try {
            this.distributedLock.lockInterruptibly();
        } catch (InterruptedException interruptedException) {
            //如果分布式锁实现没有做兜底,这里做兜底
            this.distributedLock.unlock();
            Thread.currentThread().interrupt();
            throw interruptedException;
        } catch (Exception e) {
            this.distributedLock.unlock();
            this.rethrowAsLockException(e);
        }
    }

    @Override
    public boolean tryLock() {
        try {
            return tryLock(0, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return distributedLock.tryLock(time, unit);
        }

        if (isAbsoluteTransaction()) {
            //当前事务上下文为独立的事务
            return distributedLock.tryLock(time, unit);
        }

        LockTransactionStrategy transactionStrategy = super.lockInfo.getLockTransactionStrategy();
        switch (transactionStrategy) {
            case THREAD_SAFE:
            case WARMING:
                log.warn("use lock in a transaction context may cause lock invalidation,you can use the lock transaction strategy:THREAD_SAFE or move your lock outside of a transaction context");
                return distributedLock.tryLock(time, unit);
            case FORBIDDEN:
                throw new LockTransactionForbiddenException("use lock in a transaction context is forbidden in system config.");
            default:
                return distributedLock.tryLock(time, unit);
        }
    }


    @Override
    public void unlock() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            distributedLock.unlock();
            return;
        }

        LockTransactionStrategy transactionStrategy = super.lockInfo.getLockTransactionStrategy();
        if (isAbsoluteTransaction()) {
            //当前事务上下文为独立的事务
            distributedLock.unlock();
            return;
        }

        switch (transactionStrategy) {
            case THREAD_SAFE:
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        distributedLock.unlock();
                    }
                });
                return;
            case WARMING:
                distributedLock.unlock();
                return;
            case FORBIDDEN:
                throw new LockTransactionForbiddenException("use lock in a transaction context is forbidden in system config.");
            default:
                distributedLock.unlock();
        }
    }

    private boolean isAbsoluteTransaction() {
        LockAspect.LockContext lockContext = LockAspect.getLockContext(lockInfo.getName());
        if (Objects.nonNull(lockContext)) {
            ProceedingJoinPoint joinPoint = lockContext.getJoinPoint();
            TransactionAttribute transactionAttribute = SpringContextHelper.getBean(AnnotationTransactionAttributeSource.class).getTransactionAttribute(((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getClass());
            int propagationBehavior = transactionAttribute.getPropagationBehavior();
            return Objects.equals(TransactionDefinition.PROPAGATION_REQUIRES_NEW, propagationBehavior);
        }
        return false;
    }

}