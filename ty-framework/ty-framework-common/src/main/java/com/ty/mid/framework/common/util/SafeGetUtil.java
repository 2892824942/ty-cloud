package com.ty.mid.framework.common.util;

import com.ty.mid.framework.common.pojo.KVResp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * 安全获取工具类，防npe
 */
@NoArgsConstructor
@Data
public class SafeGetUtil {


    public static <K> String getString(K obj, Function<K, String> function) {
        return get(obj, function, "");
    }

    public static <K> String getKVStringValue(KVResp<K, String> kVResp) {
        return get(kVResp, KVResp::getValue, "");
    }

    public static <K> Integer getInt(K obj, Function<K, Integer> function) {
        return get(obj, function, 0);
    }

    public static <K> Long getLong(K obj, Function<K, Long> function) {
        return get(obj, function, 0L);
    }

    public static <K, V> V get(K obj, Function<K, V> function, V defaultValue) {
        return Optional.ofNullable(obj).map(function).orElse(defaultValue);
    }

    public static <T> Collection<T> get(Collection<T> obj) {
        return Optional.ofNullable(obj).orElse(Collections.emptyList());
    }


    public static <K, V> Map<K, V> get(Map<K, V> obj) {
        return Optional.ofNullable(obj).orElse(Collections.emptyMap());
    }
}