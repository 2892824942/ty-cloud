package com.ty.mid.framework.common.pojo;


import java.util.Objects;

/**
 * 键值对
 */
public class KeyValuePair<K, V> implements KVBaseResp<K, V> {

    private final K key;

    private final V value;


    protected KeyValuePair(K key, V value) {
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
        KeyValuePair<?, ?> that = (KeyValuePair<?, ?>) o;
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
