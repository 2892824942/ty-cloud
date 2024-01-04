package com.ty.mid.framework.mybatisplus.service.cache.generic;

import cn.hutool.core.collection.CollUtil;
import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.core.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 可缓存service的定义，附带实现方法
 * 解释：实现方法本应放到下层abstract，但是由于java不支持多继承，所以使用default方法解决该问题
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
    List<S> listFromDbNeedCache();

    String getCacheName();

    Cache<String, T> getCache();

    /**
     * 重新加载缓存
     *
     * @return
     */
    default Collection<T> reloadCache() {
        log.info("reloading cache {}, with cache class: {}", getCacheName(), this.getClass().getSimpleName());

        List<S> list = this.listFromDbNeedCache();
        if (CollUtil.isEmpty(list)) {
            log.warn("cache data is empty");
        }

        log.info("fetched {} data from database", list.size());
        if (list.size() > 5000) {
            log.warn("cache size more than 5000 is not recommended,it will slow your application start up speed down!");
        }

        Collection<T> dtos = this.convert2(list);

        Map<String, T> cacheMap = dtos.stream().collect(Collectors.toMap(this::resolveMapKey, Function.identity()));
        getCache().putAll(cacheMap);
        log.info("cache {} reloaded.", getCacheName());
        return dtos;
    }

    /**
     * 清空缓存
     *
     * @return
     */
    default void clearCache() {
        Cache<String, T> cache = getCache();
        cache.removeAll();
    }


    /**
     * 获取所有数据
     *
     * @return
     */
    default Map<String, T> getAll(Set<String> keys) {
        return getCache().getAll(keys);
    }


    /**
     * 是否拥有某条数据
     *
     * @return
     */
    default boolean containsKey(String key) {
        return getCache().containsKey(key);
    }

    /**
     * 基于 key 获取缓存数据
     *
     * @param key
     * @return
     */
    default T getByCacheKey(String key) {
        Validator.requireNonEmpty(key, "key不能为空");
        T ret = getCache().get(key);
        log.info("get data in map cache [{}], key [{}], and cache {}", getCacheName(), key, ret == null ? "miss" : "found");
        return ret;
    }

    String resolveMapKey(T dto);

}
