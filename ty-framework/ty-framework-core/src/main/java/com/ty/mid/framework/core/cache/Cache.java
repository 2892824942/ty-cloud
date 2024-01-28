package com.ty.mid.framework.core.cache;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 后续替换为java的实现
 *
 * @param <K>
 * @param <V>
 */
public interface Cache<K, V> {
    @Nullable
    Collection<V> getCollection(K key);

    /**
     * 数据追加
     *
     * @param key
     * @param value
     */
    void addAll(K key, Collection<V> value);

    /**
     * 直接覆盖
     *
     * @param key
     * @param value
     */
    void add(K key, Collection<V> value);

//**************以上为collection对应api,下方是普通value对应api**************

    @Nullable
    V get(K key);

    boolean existsKey(K key);

    void put(K key, V value);

    void put(K key, V value, TimeUnit timeUnit, Long expiration);

    V putIfAbsent(K key, V value, TimeUnit timeUnit, Long expiration);

    V computeIfAbsent(K key, V value, TimeUnit timeUnit, Long expiration);

    /**
     * 直接覆盖
     *
     * @param key
     * @param value
     * @param timeUnit
     * @param expireAt
     */
    void put(K key, V value, TimeUnit timeUnit, Date expireAt);


    List<V> getAll(Collection<K> keys);

    void invalidate(K key);

    void invalidateAll(Collection<K> keys);

    Date getExpireAt(K key);

    boolean renewCache(K cacheKey, long renewTime, TimeUnit timeUnit);

}
