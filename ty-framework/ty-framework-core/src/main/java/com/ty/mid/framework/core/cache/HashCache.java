package com.ty.mid.framework.core.cache;

import org.springframework.lang.Nullable;

import java.util.Map;

@Deprecated
public interface HashCache<K, IK, V> extends Cache<K, V> {

    @Nullable
    V get(K key, IK innerKey);

    /**
     * 外部数据追加，内部innerKey相同则覆盖
     *
     * @param key
     * @param innerKey
     * @param value
     */
    void put(K key, IK innerKey, V value);


    /**
     * 直接覆盖
     *
     * @param key
     * @param m
     */
    void put(K key, Map<IK, V> m);

    /**
     * 数据追加
     *
     * @param key
     * @param m
     */
    Map<IK, V> putAll(K key, Map<IK, V> m);

    Map<IK, V> getMapAll(K key);

    void invalidate(K key, IK innerKey);
}
