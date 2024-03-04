package com.ty.mid.framework.cache.support;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;

import java.util.*;

@Slf4j
public class MultiCacheManagerKeeper implements CacheManager {
    final Map<String, CacheManager> cacheNameMangerMap = new HashMap<>();
    CachePlusConfig cacheConfig;
    private final ObjectProvider<CacheManager> cacheManagers;


    public MultiCacheManagerKeeper(CachePlusConfig cacheConfig, ObjectProvider<CacheManager> cacheManagers) {
        this.cacheConfig = cacheConfig;
        this.cacheManagers = cacheManagers;
    }

    @Override
    public Cache getCache(String name) {

        CacheManager cacheManager = this.getManagerByCacheName(name);
        //TODO 增加一个default配置,而不是直接报异常
        //代码配置优先级最高
        if (Objects.nonNull(cacheManager)) {
            return cacheManager.getCache(name);
        }
        throw new FrameworkException("not match cache manager from cacheName:" + name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheNameMangerMap.keySet();
    }


    private CacheManager getManagerByCacheName(String name) {
        CacheManager cacheManager = cacheNameMangerMap.get(name);
        if (Objects.nonNull(cacheManager)) {
            return cacheManager;
        }
        cacheManagers.stream().forEach(manager -> manager.getCacheNames().forEach(cacheName -> cacheNameMangerMap.put(cacheName, manager)));
        if (log.isDebugEnabled()){

            log.debug("cache:allCacheNames:{},cacheManagers:{}", cacheNameMangerMap.keySet(),  "["+StrUtil.join(",", cacheManagers.stream().map(Object::getClass).map(Class::getSimpleName).toArray())+"]");
            log.debug("cache name:{} match cacheManage:{}", name, Optional.ofNullable(cacheNameMangerMap.get(name)).map(Object::getClass).map(Class::getSimpleName).orElse("null"));
        }
        return cacheNameMangerMap.get(name);
    }
}
