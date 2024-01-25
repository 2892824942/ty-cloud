package com.ty.mid.framework.cache.support.manager.decorator;

import com.ty.mid.framework.cache.model.NullValue;
import com.ty.mid.framework.cache.util.ThreadResourceUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;

public class CustomTransactionAwareCacheDecorator extends TransactionAwareCacheDecorator {

    /**
     * Create a new TransactionAwareCache for the given target Cache.
     *
     * @param targetCache the target Cache to decorate
     */
    public CustomTransactionAwareCacheDecorator(Cache targetCache) {
        super(targetCache);
    }

    /**
     * 解决RR隔离级别，使用缓存后，get（cacheable）方法变为RC的问题。
     * 解决方式：增加事务上下文缓存
     *
     * @param key
     * @return
     */
    @Override
    public ValueWrapper get(Object key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {


            Integer currentTransactionIsolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
            //没有指定，默认按照RR处理，如果业务比较特殊，默认不是使用RR，则需要在事务注解指定，或重写此方法，以屏蔽事务上下文级别的缓存
            if (Objects.isNull(currentTransactionIsolationLevel) || TransactionDefinition.ISOLATION_REPEATABLE_READ == currentTransactionIsolationLevel) {
                //保证事务的可重复读
                if (ThreadResourceUtil.hasResource(getCacheName(), key)) {

                    Object resource = ThreadResourceUtil.getResource(getCacheName(), key);
                    if (Objects.equals(NullValue.INSTANCE, resource)) {
                        return NullValue.INSTANCE;
                    }
                    return new SimpleValueWrapper(resource);
                }
                //事务上下文中不存在，则从远程获取
                ValueWrapper valueWrapper = super.get(key);
                //本地存储null数据，防止inert后多次get（CacheAble）,浪费资源
                if (Objects.isNull(valueWrapper) || Objects.isNull(valueWrapper.get())) {
                    ThreadResourceUtil.bindResource(getCacheName(), key, NullValue.INSTANCE);
                } else {
                    Object value = valueWrapper.get();
                    assert value != null;
                    //兼容底层实现依赖nullValue的配置
                    if (value.equals(org.springframework.cache.support.NullValue.INSTANCE)) {
                        ThreadResourceUtil.bindResource(getCacheName(), key, NullValue.INSTANCE);
                    } else {
                        ThreadResourceUtil.bindResource(getCacheName(), key, valueWrapper.get());
                    }

                }
                //resource不会随着事务提交清理，需要手动清理
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void beforeCommit(boolean readOnly) {
                        ThreadResourceUtil.removeAllResource();
                    }
                });
                return valueWrapper;
            }
        }
        return super.get(key);
    }

    //    private static <T> T valueFromLoader(Object key, Callable<T> valueLoader) {
//
//        try {
//            return valueLoader.call();
//        } catch (Exception e) {
//            throw new ValueRetrievalException(key, valueLoader, e);
//        }
//    }
    @Override
    public void put(Object key, Object value) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            //事务开启情况下，数据在事务上下文缓存。
            ThreadResourceUtil.bindResource(getCacheName(), key, value);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    ThreadResourceUtil.removeAllResource();
                }

                @Override
                public void afterCommit() {
                    CustomTransactionAwareCacheDecorator.super.getTargetCache().put(key, value);
                }
            });
        } else {
            CustomTransactionAwareCacheDecorator.super.getTargetCache().put(key, value);
        }
    }

    @Override
    public void evict(Object key) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            //事务开启情况下，数据在事务上下文缓存。存入null会到时get走redis，这里存储NullValue
            ThreadResourceUtil.bindResource(getCacheName(), key, NullValue.INSTANCE);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    ThreadResourceUtil.removeAllResource();
                }

                @Override
                public void afterCommit() {
                    CustomTransactionAwareCacheDecorator.super.getTargetCache().put(key, org.springframework.cache.support.NullValue.INSTANCE);
                }
            });
        } else {
            CustomTransactionAwareCacheDecorator.super.getTargetCache().evict(key);
        }
    }


    @Override
    public void clear() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            //事务开启情况下，数据在事务上下文缓存。存入null会导致get走redis，这里存储NullValue
            ThreadResourceUtil.bindNameSpaceNullValueResource(getCacheName());
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    ThreadResourceUtil.removeAllResource();
                }

                @Override
                public void afterCommit() {
                    CustomTransactionAwareCacheDecorator.super.getTargetCache().clear();
                }
            });
        } else {
            CustomTransactionAwareCacheDecorator.super.getTargetCache().clear();
        }
    }

    @Override
    public boolean invalidate() {
        ThreadResourceUtil.unBindNameSpaceResource(getCacheName());
        return CustomTransactionAwareCacheDecorator.super.getTargetCache().invalidate();
    }


    private String getCacheName() {
        return CustomTransactionAwareCacheDecorator.super.getTargetCache().getName();
    }

}
