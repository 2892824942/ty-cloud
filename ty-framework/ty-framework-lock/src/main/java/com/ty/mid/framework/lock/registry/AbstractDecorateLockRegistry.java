package com.ty.mid.framework.lock.registry;

import cn.hutool.core.collection.CollUtil;
import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.core.LockInfoProvider;
import com.ty.mid.framework.lock.decorator.*;
import com.ty.mid.framework.lock.decorator.cycle.CycleDetectingLockDecorator;
import com.ty.mid.framework.lock.enums.LockType;
import com.ty.mid.framework.lock.factory.AdapterLockFactory;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.strategy.CycleLockStrategy;
import com.ty.mid.framework.lock.strategy.LockTransactionStrategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.locks.Lock;

@Slf4j
@Getter
public abstract class AbstractDecorateLockRegistry implements TypeLockRegistry {

    private final LockFactory lockFactory;
    @Resource
    private LockConfig lockConfig;

    @Resource
    private LockInfoProvider lockInfoProvider;

    public AbstractDecorateLockRegistry(LockFactory lockFactory) {
        this.lockFactory = lockFactory;

    }

    public Lock doGetLock(LockInfo lockInfo) {
        Lock lock = lockFactory.getLock(Optional.ofNullable(lockInfo.getType())
                .map(LockType::getCode).orElse(null), lockInfo.getName());
        log.debug("1.success obtain lock:{}", lock.getClass().getSimpleName());

        //首先装载adapterFactory对应处理器
        if (lockFactory instanceof AdapterLockFactory) {
            AdapterLockFactory adapterLockFactory = (AdapterLockFactory) lockFactory;
            lock = new LockAdapterDecorator(lock, lockInfo, adapterLockFactory.getAdapter());
        }
        //然后装载过程处理装饰器
        lock = new LockProcessHandleDecorator(lock, lockInfo);
        //最后装载功能型装饰器 ---功能型装饰器内部异常不受过程处理装饰器控制
        if (lockInfo.getWithLocalCache()) {
            lock = new LocalCacheLockDecorator(lock, lockInfo);
        }
        if (!Objects.equals(lockInfo.getLockTransactionStrategy(), LockTransactionStrategy.DISABLED)) {
            lock = new TransactionLockDecorator(lock, lockInfo);
        }

        if (!Objects.equals(lockInfo.getCycleLockStrategy(), CycleLockStrategy.DISABLED)) {
            lock = new CycleDetectingLockDecorator(lock, lockInfo);
        }
        printLog(lock);
        return lock;
    }

    @Override
    public Lock obtain(Object lockKey) {
        return this.obtain(null, lockKey);
    }

    @Override
    public Lock obtain(String type, Object lockKey) {
        Assert.isInstanceOf(String.class, lockKey);
        String lockKeyStr = lockKey.toString();
        LockInfo lockInfo = lockInfoProvider.transform2(lockConfig, type, lockKeyStr);
        return doGetLock(lockInfo);
    }

    private void printLog(Lock lock) {
        if (log.isDebugEnabled()) {
            StringJoiner stringJoiner = new StringJoiner("->");
            stringJoiner.add("2.lock decorator chain is ");
            Lock innerLock = lock;
            List<String> strList = new ArrayList<>();
            do {
                AbstractLockDecorator abstractLockDecorator = (AbstractLockDecorator) innerLock;
                strList.add("[" + abstractLockDecorator.getClass().getSimpleName() + "]");
                innerLock = abstractLockDecorator.getRealLock();
            } while (innerLock instanceof AbstractLockDecorator);
            strList.add("[" + innerLock.getClass().getSimpleName() + "]");
            List<String> reverseStr = CollUtil.reverse(strList);
            reverseStr.forEach(stringJoiner::add);
            log.debug(stringJoiner.toString());
        }
    }
}
