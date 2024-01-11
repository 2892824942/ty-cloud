package com.ty.mid.framework.service.cache.generic;

import cn.hutool.core.collection.CollUtil;
import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.core.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 可缓存service的定义，附带实现方法
 *
 * @param <S>
 * @param <T>
 */
public interface BaseCacheService<S, T> extends Converter<S, T> {
    Logger log = LoggerFactory.getLogger(BaseCacheService.class);


    /**
     * 查询需要缓存的数据
     *
     * @return
     */
    List<S> cacheLoadListFromDb();


    /**
     * 查询需要缓存的数据
     *
     * @return
     */
    List<S> cacheLoadListFromDb(Iterator<String> keys);


    /**
     * 查询需要缓存的数据 单个
     *
     * @return
     */
    S cacheLoadFromDb(String key);

    String getCacheName();

    Cache<String, T> getCache();

    /**
     * 间隔符定义
     */

    @NotNull
    String cacheDelimiter();

    /**
     * 定义更新的key,
     * 默认使用id作为key,仅支持单一的key
     *
     * @return
     */
    Function<S, ?> cacheDefineDOMapKey();


    /**
     * 定义更新的key
     * 支持多个字段,默认使用间隔符按照顺序拼接
     *
     * @return
     * @link com.ty.mid.framework.service.cache.generic.BaseCacheService#cacheDelimiter()
     */
    List<? extends Function<S, ?>> cacheDefineDOMapKeys();

    /**
     * 重新加载缓存
     *
     * @return
     */
    default Collection<T> cacheReload() {
        log.info("reloading cache {}, with cache class: {}", getCacheName(), this.getClass().getSimpleName());

        List<S> list = this.cacheLoadListFromDb();
        if (CollUtil.isEmpty(list)) {
            log.warn("cache data is empty");
            return Collections.emptyList();
        }

        log.info("fetched {} data from database", list.size());

        Collection<T> dtos = this.convert2(list);

        Map<String, T> cacheMap = dtos.stream().collect(Collectors.toMap(this.getKeyFunction(), Function.identity(), (a, b) -> b));
        getCache().putAll(cacheMap);
        log.info("cache {} reloaded.", getCacheName());
        return dtos;
    }

    default boolean isMultiColumn() {
        return CollUtil.isNotEmpty(cacheDefineDOMapKeys());
    }

    default Function<T, String> getKeyFunction() {
        if (isMultiColumn()) {
            //多个字段组合,通过间隔符链接
            List<Function<T, String>> functions = cacheDefineDTOMapKeys();
            return t -> functions.stream().map(function -> function.apply(t)).collect(Collectors.joining(cacheDelimiter()));
        }
        return cacheDefineDTOMapKey();
    }

    /**
     * 获取db数据
     *
     * @return
     */
    default Map<String, T> getDbDataMap(Iterator<String> keys) {
        List<S> list = this.cacheLoadListFromDb(keys);
        return getDbDataMap(list);
    }

    /**
     * 获取db数据
     *
     * @return
     */
    default Map<String, T> getDbDataMap(List<S> list) {

        if (CollUtil.isEmpty(list)) {
            log.warn("cache data is empty");
            return Collections.emptyMap();
        }

        log.info("fetched {} data from database", list.size());
        if (list.size() > 5000) {
            log.warn("cache size more than 5000 is not recommended,it will slow down your application start up speed !");
        }

        Collection<T> dtos = this.convert2(list);

        return dtos.stream().collect(Collectors.toMap(this.getKeyFunction(), Function.identity(), (a, b) -> b));
    }


    /**
     * 重新加载缓存
     *
     * @return
     */
    default T getDbData(String key) {

        S dataFromDbNeedCache = this.cacheLoadFromDb(key);
        if (Objects.isNull(dataFromDbNeedCache)) {
            log.warn("cache data is empty");
            return null;
        }
        return this.convert2(dataFromDbNeedCache);
    }

    /**
     * 清空缓存
     *
     * @return
     */
    default void cacheClear() {
        Cache<String, T> cache = getCache();
        cache.removeAll();
    }

    /**
     * 清空某些缓存
     *
     * @return
     */
    default void cacheClear(Collection<S> sourceList) {
        if (CollUtil.isEmpty(sourceList)) {
            return;
        }
        Collection<T> dtos = this.convert2(sourceList);
        Set<String> keySet = dtos.stream().map(source -> getKeyFunction().apply(source)).collect(Collectors.toSet());
        getCache().removeAll(keySet);
    }

    /**
     * 清空某个缓存
     *
     * @return
     */
    default void cacheClear(S source) {
        if (Objects.isNull(source)) {
            return;
        }
        T dto = this.convert2(source);
        String key = getKeyFunction().apply(dto);
        getCache().remove(key);
    }


    /**
     * 获取所有数据
     *
     * @return
     */
    default Map<String, T> cacheGetAll(Collection<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return Collections.emptyMap();
        }
        return getCache().getAll(new HashSet<>(keys));
    }


    /**
     * 是否拥有某条数据
     *
     * @return
     */
    default boolean cacheContainsKey(String key) {
        return getCache().containsKey(key);
    }

    /**
     * 基于 key 获取缓存数据
     *
     * @param key
     * @return
     */
    default T cacheGetByKey(String key) {
        Validator.requireNonEmpty(key, "key不能为空");
        T ret = getCache().get(key);
        log.info("get data in map cache [{}], key [{}], and cache {}", getCacheName(), key, ret == null ? "miss" : "found");
        return ret;
    }

    default Function<T, String> cacheDefineDTOMapKey() {
        Function<S, ?> function = cacheDefineDOMapKey();
        return t -> {
            S s = rConvert2(t);
            return String.valueOf(function.apply(s));
        };
    }

    default List<Function<T, String>> cacheDefineDTOMapKeys() {
        List<? extends Function<S, ?>> functions = cacheDefineDOMapKeys();
        return functions.stream().map(function -> (Function<T, String>) t -> {
            S s = rConvert2(t);
            return String.valueOf(function.apply(s));
        }).collect(Collectors.toList());
    }


}
