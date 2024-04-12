package com.ty.mid.framework.common.pojo;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/**
 * 键值对
 */
public class KVPair<K, V> implements KVResp<K, V> {
    @Schema(description = "键值: 键")
    private final K key;
    @Schema(description = "键值: 值")
    private final V value;


    protected KVPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KVPair<?, ?> that = (KVPair<?, ?>) o;
        return key.equals(that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }
}
