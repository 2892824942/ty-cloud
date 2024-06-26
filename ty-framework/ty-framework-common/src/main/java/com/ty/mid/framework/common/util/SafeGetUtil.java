package com.ty.mid.framework.common.util;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.common.lang.NeverNull;
import com.ty.mid.framework.common.pojo.KVResp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 安全获取工具类，防npe
 */
@NoArgsConstructor
@Data
public class SafeGetUtil {


    public static <K> @NeverNull String getString(K obj, Function<K, String> function) {
        return get(obj, function, "");
    }

    public static <K> @NeverNull Integer getInt(K obj, Function<K, Integer> function) {
        return get(obj, function, 0);
    }

    public static <K> @NeverNull Long getLong(K obj, Function<K, Long> function) {
        return get(obj, function, 0L);
    }

    public static @NeverNull String get(String obj) {
        return Optional.ofNullable(obj).orElse(StrUtil.EMPTY);
    }

    public static @NeverNull String getString(String obj) {
        return get(obj);
    }

    public static @NeverNull String getOrDefault(String obj, String defaultStr) {
        return Opt.ofBlankAble(obj).orElse(defaultStr);
    }

    public static @NeverNull <T> T getOrDefault(T obj, Supplier<? extends T> defaultSupplier) {
        return Opt.ofNullable(obj).orElseGet(defaultSupplier);
    }

    public static @NeverNull <T, R extends Collection<T>> R getOrDefault(R obj, Supplier<? extends R> defaultSupplier) {
        return Opt.ofEmptyAble(obj).orElseGet(defaultSupplier);
    }

    public static @NeverNull <K> String getKVStringValue(KVResp<K, String> kVResp) {
        return get(kVResp, KVResp::getValue, "");
    }


    public static @NeverNull <K, V> V get(K obj, Function<K, V> function, V defaultValue) {
        return Optional.ofNullable(obj).map(function).orElse(defaultValue);
    }

    public static @NeverNull <T, R> Collection<T> get(Collection<T> obj) {
        return Optional.ofNullable(obj).orElse(Collections.emptyList());
    }


    public static @NeverNull <K, V> Map<K, V> get(Map<K, V> obj) {
        return Optional.ofNullable(obj).orElse(Collections.emptyMap());
    }


}