package com.ty.mid.framework.cache.service;

import com.ty.mid.framework.cache.constant.CacheNameConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class
SimpleCacheTestService {
    private final Map<String, String> enties = new HashMap<>();
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public SimpleCacheTestService() {
        enties.put("1", "this no 1");
    }


    @Cacheable(cacheNames = CacheNameConstant.MULTI)
    public String get(String id) {
        // 记录数据产生的时间，用于测试对比
        long time = new Date().getTime();
        // 当数据不是从cache里面获取时，打印日志
        log.info("Get key:{} value from db", id);
        return "Get value by id=" + id + ",the value is " + enties.get(id);
    }


    @Cacheable(cacheNames = CacheNameConstant.MULTI, key = "#id")
    public String getWithCache(String id) {
        // 当数据不是从cache里面获取时，打印日志
        log.info("Get key:{} value from db", id);
        return "Get value by id=" + id + ",the value is " + enties.get(id);
    }


    @CacheEvict(cacheNames = CacheNameConstant.MULTI)
    public String delete(String id) {

        return enties.remove(id);
    }


    @CachePut(cacheNames = CacheNameConstant.MULTI, key = "#id")
    public String save(String id, String value) {
        log.info("save value " + value + " with key " + id);
        enties.put(id, value);
        return value;
    }


    @CachePut(cacheNames = CacheNameConstant.MULTI, key = "#id")
    public String update(String id, String value) {
        return enties.compute(id, (a, b) -> value);
    }

    @CachePut(cacheNames = CacheNameConstant.TEST, key = "#id")
    public String updateNoMatch(String id, String value) {
        enties.compute(id, (a, b) -> value);
        return "hahaha";
    }

}
