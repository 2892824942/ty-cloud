package com.ty.mid.framework.cache.support;

import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.common.exception.FrameworkException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class MultiCacheManagerKeeper implements CacheManager {
    final Map<String, CacheManager> cacheNameMangerMap = new HashMap<>();
    CachePlusConfig cacheConfig;
    private ObjectProvider<CacheManager> cacheManagers;


    public MultiCacheManagerKeeper(CachePlusConfig cacheConfig, ObjectProvider<CacheManager> cacheManagers) {
        this.cacheConfig = cacheConfig;
        this.cacheManagers = cacheManagers;
    }

    @Override
    public Cache getCache(String name) {

        CacheManager cacheManager = this.getManagerByCacheName(name);
        log.debug("cache------------:cacheNameMangerMap:{},cacheManagers:{}", cacheNameMangerMap.keySet(), cacheManagers.stream().toArray());


        log.debug("cacheNames------------:getCacheNames:{}", cacheManagers.stream().map(CacheManager::getCacheNames).flatMap(Collection::stream).toArray());

        //代码配置优先级最高
        if (Objects.nonNull(cacheManager)) {

            log.debug("cache name match cacheManage by code,name:{},manager:{}", name, cacheManager);
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
        return cacheNameMangerMap.get(name);
    }
}
