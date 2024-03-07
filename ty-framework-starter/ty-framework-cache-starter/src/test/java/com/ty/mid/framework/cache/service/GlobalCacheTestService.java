package com.ty.mid.framework.cache.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@CacheConfig(cacheNames = CacheNameConstant.TEST)
public class GlobalCacheTestService {


    private final Map<String, String> enties = new HashMap<>();

    public GlobalCacheTestService() {
        enties.put("1", "this no 1");
    }

    @Cacheable
    public String get(String id) {
        // 记录数据产生的时间，用于测试对比
        // 当数据不是从cache里面获取时，打印日志
        return "Get value by id=" + id + ",the value is " + enties.get(id);
    }

    @Cacheable(key = "#id")
    public String getWithCache(String id) {
        // 记录数据产生的时间，用于测试对比
        long time = new Date().getTime();
        // 打印使用到的cacheManager
        // 当数据不是从cache里面获取时，打印日志
        return "Get value by id=" + id + ",the value is " + enties.get(id);
    }

    /**
     * 要求缓存为空时，访问数据必须串行
     *
     * @param id
     * @return
     */
    @Cacheable(key = "#id", sync = true)
    public String getWithSyncInitCache(String id) {
        // 记录数据产生的时间，用于测试对比
        long time = new Date().getTime();
        // 当数据不是从cache里面获取时，打印日志
        return "Get value by id=" + id + ",the value is " + enties.get(id);
    }

    /**
     * 如果方法内部异常，会导致缓存无法删除，为了数据一致性，这里选择方法执行前直接清除缓存
     *
     * @param id
     * @return
     */
    @CacheEvict(beforeInvocation = true)
    public String delete(String id) {

        return enties.remove(id);
    }


    @CachePut(key = "#id")
    public String save(String id, String value) {
        enties.put(id, value);
        return value;
    }


    @CachePut(key = "#id")
    public String update(String id, String value) {
        return enties.compute(id, (a, b) -> value);

    }

    @CachePut(key = "#id")
    public String updateNoMatch(String id, String value) {
        enties.compute(id, (a, b) -> value);
        return "hahaha";
    }

}
