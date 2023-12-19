package com.ty.mid.framework.common.dto;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 键值对
 *
 * @param <K>
 * @param <V>
 */
@Getter
public class KeyValueDTO<K extends Serializable, V extends Serializable> extends AbstractDTO {
    private K key;

    private V value;

    public KeyValueDTO(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
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
        KeyValueDTO<?, ?> that = (KeyValueDTO<?, ?>) o;
        return key.equals(that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
