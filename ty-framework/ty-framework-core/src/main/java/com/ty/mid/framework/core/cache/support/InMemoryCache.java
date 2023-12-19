package com.ty.mid.framework.core.cache.support;


import com.ty.mid.framework.common.lang.ThreadSafe;
import com.ty.mid.framework.core.cache.HashCache;
import com.ty.mid.framework.core.util.ExpireCacheInMemory;
import com.ty.mid.framework.core.util.StringUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
@Slf4j
@Deprecated
public class InMemoryCache<K, IK, T> implements HashCache<K, IK, T> {
    ExpireCacheInMemory<K, T> cacheInMemory = new ExpireCacheInMemory<>();
    ExpireCacheInMemory<K, Collection<T>> cacheCollectionInMemory = new ExpireCacheInMemory<>();
    ExpireCacheInMemory<K, ExpireCacheInMemory<IK, T>> cacheMapInMemory = new ExpireCacheInMemory<>();

    public InMemoryCache(boolean openClearLRU) {
        cacheInMemory.setCleanLRU(openClearLRU);
        cacheCollectionInMemory.setCleanLRU(openClearLRU);
        cacheMapInMemory.setCleanLRU(openClearLRU);
    }

    public void setClearLRU(boolean open) {
        cacheInMemory.setCleanLRU(open);
        cacheCollectionInMemory.setCleanLRU(open);
        cacheMapInMemory.setCleanLRU(open);
    }

    @Override
    @ThreadSafe
    public T get(K key) {
        return cacheInMemory.getCache(key);
    }

    @Override
    @ThreadSafe
    public Collection<T> getCollection(K key) {
        return cacheCollectionInMemory.getCache(key);
    }

    @Override
    @ThreadSafe
    public boolean existsKey(K key) {
        return cacheInMemory.isExist(key);
    }

    @Override
    @ThreadSafe
    public void put(K key, T value) {
        cacheInMemory.setCache(key, value);
    }

    @Override
    @ThreadSafe
    public void put(K key, T value, TimeUnit timeUnit, Long expiration) {
        cacheInMemory.setCache(key, value, timeUnit, expiration);
    }

    /**
     * 本地的这个方法，返回值支持的不好,不要使用返回值
     *
     * @param key
     * @param value
     * @param timeUnit
     * @param expiration
     * @return
     */
    @Override
    @ThreadSafe
    public T putIfAbsent(K key, T value, TimeUnit timeUnit, Long expiration) {
        return cacheInMemory.setCacheComputeIfAbsent(key, value, timeUnit, expiration);
    }

    @Override
    @ThreadSafe
    public T computeIfAbsent(K key, T value, TimeUnit timeUnit, Long expiration) {
        return cacheInMemory.setCacheComputeIfAbsent(key, value, timeUnit, expiration);
    }

    @Override
    @ThreadSafe
    public void put(K key, T value, TimeUnit timeUnit, Date expireAt) {
        long milliSeconds = expireAt.getTime() - System.currentTimeMillis();
        if (milliSeconds > 0) {
            cacheInMemory.setCache(key, value, TimeUnit.MILLISECONDS, milliSeconds);
        }
    }

    /**
     * 方法线程安全性取决于使用的Collection
     *
     * @param key
     * @param value
     */
    @Override
    public void addAll(K key, Collection<T> value) {
        Collection<T> cacheCollection = cacheCollectionInMemory.setCacheComputeIfAbsent(key, value);
        if (!value.equals(cacheCollection)) {
            cacheCollection.addAll(value);
        }
    }

    /**
     * 方法线程安全性取决于使用的Collection
     *
     * @param key
     * @param value
     */
    @Override
    public void add(K key, Collection<T> value) {
        cacheCollectionInMemory.setCache(key, value);
    }


    @Override
    @ThreadSafe
    public List<T> getAll(Collection<K> keys) {
        return cacheInMemory.getAllCache(keys);
    }

    @Override
    @ThreadSafe
    public T get(K key, IK innerKey) {
        ExpireCacheInMemory<IK, T> cache = cacheMapInMemory.getCache(key);
        if (cache.getCacheSize() <= 0) {
            return null;
        }
        return cache.getCache(innerKey);
    }

    @Override
    @ThreadSafe
    public void put(K key, IK innerKe, T value) {

        ExpireCacheInMemory<IK, T> innerCache = new ExpireCacheInMemory<>();
        innerCache.setCache(innerKe, value);
        //注意，这里使用的是setCacheComputeIfAbsent
        ExpireCacheInMemory<IK, T> cacheInMemory = cacheMapInMemory.setCacheComputeIfAbsent(key, innerCache);
        if (!cacheInMemory.equals(innerCache)) {
            cacheInMemory.setCacheAll(innerCache.getCacheMapType());
        }

    }

    @Override
    @ThreadSafe
    public void put(K key, Map<IK, T> m) {
        ExpireCacheInMemory<IK, T> innerCache = new ExpireCacheInMemory<>();
        innerCache.setCacheAll(m);
        //注意，这里使用的是setCacheComputeIfAbsent
        cacheMapInMemory.setCache(key, innerCache);
    }

    @Override
    @ThreadSafe
    public Map<IK, T> putAll(K key, Map<IK, T> m) {
        ExpireCacheInMemory<IK, T> innerCache = new ExpireCacheInMemory<>();
        innerCache.setCacheAll(m);
        //注意，这里使用的是setCacheComputeIfAbsent
        ExpireCacheInMemory<IK, T> cacheInMemory = cacheMapInMemory.setCacheComputeIfAbsent(key, innerCache);
        if (!cacheInMemory.equals(innerCache)) {
            cacheInMemory.setCacheAll(innerCache.getCacheMapType());
        }
        return cacheInMemory.getCacheMapType();
    }


    @Override
    @ThreadSafe
    public Map<IK, T> getMapAll(K key) {
        return cacheMapInMemory.getCache(key).getCacheMapType();
    }


    @Override
    @ThreadSafe
    public void invalidate(K key, IK innerKey) {
        ExpireCacheInMemory<IK, T> cache = cacheMapInMemory.getCache(key);
        if (cache.getCacheSize() <= 0) {
            return;
        }
        cache.deleteCache(innerKey);
    }

    @Override
    @ThreadSafe
    public void invalidate(K key) {
        cacheInMemory.deleteCache(key);
        cacheCollectionInMemory.deleteCache(key);
        cacheMapInMemory.deleteCache(key);
    }

    @Override
    @ThreadSafe
    public void invalidateAll(Collection<K> keys) {
        cacheInMemory.deleteAllCache(keys);
        cacheCollectionInMemory.deleteAllCache(keys);
        cacheMapInMemory.deleteAllCache(keys);
    }

    @Override
    @ThreadSafe
    public Date getExpireAt(K key) {
        Long cacheTTL = cacheInMemory.getCacheTTL(key);
        if (Objects.isNull(cacheTTL) || cacheTTL <= 0L) {
            return null;
        }
        return new Date(cacheTTL);
    }

    @Override
    @ThreadSafe
    public boolean renewCache(K cacheKey, long renewTime, TimeUnit timeUnit) {
        T cacheValue = cacheInMemory.getCache(cacheKey);
        if (StringUtils.isEmpty(cacheValue)) {
            return false;
        }
        cacheInMemory.setCacheComputeIfAbsent(cacheKey, cacheValue, timeUnit, renewTime);
        return true;
    }
}
