package com.ty.mid.framework.cache.support;


import com.ty.mid.framework.core.cache.HashCache;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Deprecated
public class NoneCache<K, IK, T> implements HashCache<K, IK, T> {


    @Override
    public T get(K key, IK innerKey) {
        return null;
    }

    @Override
    public void put(K key, IK innerKey, T value) {

    }

    @Override
    public void put(K key, Map<IK, T> m) {
        return;
    }

    @Override
    public Map<IK, T> putAll(K key, Map<IK, T> m) {
        return null;
    }

    @Override
    public T get(K key) {
        return null;
    }

    @Override
    public Collection<T> getCollection(K key) {
        return null;
    }

    @Override
    public boolean existsKey(K key) {
        return false;
    }

    @Override
    public void put(K key, T value) {

    }

    @Override
    public void put(K key, T value, TimeUnit timeUnit, Long expiration) {

    }

    @Override
    public T putIfAbsent(K key, T value, TimeUnit timeUnit, Long expiration) {
        return null;
    }

    @Override
    public T computeIfAbsent(K key, T value, TimeUnit timeUnit, Long expiration) {
        return null;
    }

    @Override
    public void put(K key, T value, TimeUnit timeUnit, Date expireAt) {

    }

    @Override
    public void addAll(K key, Collection<T> value) {

    }

    @Override
    public void add(K key, Collection<T> value) {
        return;
    }

    @Override
    public List<T> getAll(Collection<K> keys) {
        return null;
    }

    @Override
    public void invalidate(K key) {

    }

    @Override
    public void invalidateAll(Collection<K> keys) {

    }

    @Override
    public Date getExpireAt(K key) {
        return null;
    }

    @Override
    public boolean renewCache(K cacheKey, long renewTime, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public Map<IK, T> getMapAll(K key) {
        return null;
    }

    @Override
    public void invalidate(K key, IK innerKey) {

    }
}
