package com.ty.mid.framework.cache.support.manager.proxy;

import com.ty.mid.framework.cache.support.manager.decorator.CustomTransactionAwareCacheDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.util.Assert;

@Slf4j
public class CustomTransactionAwareCacheManagerProxy extends TransactionAwareCacheManagerProxy {
    protected CacheManager targetCacheManager;

    public CustomTransactionAwareCacheManagerProxy(CacheManager targetCacheManager) {
        super(targetCacheManager);
        this.targetCacheManager = targetCacheManager;
    }

    @Override
    public Cache getCache(String name) {
        Assert.state(targetCacheManager != null, "No target CacheManager set");
        Cache targetCache = this.targetCacheManager.getCache(name);
        return (targetCache != null ? new CustomTransactionAwareCacheDecorator(targetCache) : null);
    }

}
