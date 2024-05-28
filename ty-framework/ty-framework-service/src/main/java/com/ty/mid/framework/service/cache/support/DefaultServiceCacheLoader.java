package com.ty.mid.framework.service.cache.support;

import cn.hutool.core.lang.Assert;
import com.ty.mid.framework.common.dto.BaseDTO;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.service.cache.ServiceCacheLoader;
import com.ty.mid.framework.service.cache.generic.BaseCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * cache加载器 <p>
 * 用于获取全局的基于CacheService实现的所有的缓存 <p>
 * 用途: <p>
 * 1.可统一处理缓存过期刷新逻辑,比如使用job跑批更新缓存或删除缓存
 */
@Slf4j
public class DefaultServiceCacheLoader implements ServiceCacheLoader {

    @Autowired(required = false)
    private List<BaseCacheService<? extends BaseIdDO<?>, ? extends BaseDTO>> cacheServices = new ArrayList<>();

    @Override
    public void init() {
        // do nothing here, each cache service will reload cache themselves
    }

    @Override
    public Set<String> getCacheNames() {
        return cacheServices.stream().map(BaseCacheService::getCacheName).collect(Collectors.toSet());
    }

    @Override
    public void reloadCache(String cacheName) {
        this.resolveService(cacheName).cacheReload();
        log.info("successfully reload cache: {}", cacheName);
    }

    @Override
    public void clearCache(String cacheName) {
        this.resolveService(cacheName).cacheClear();
        log.info("successfully clear cache: {}", cacheName);
    }

    protected BaseCacheService<? extends BaseIdDO<?>, ? extends BaseDTO> resolveService(String cacheName) {
        BaseCacheService<? extends BaseIdDO<?>, ? extends BaseDTO> service = cacheServices.stream().filter(c -> c.getCacheName().equals(cacheName)).findFirst().orElse(null);
        Assert.notEmpty(cacheName, "缓存 [%s] 不存在", cacheName);
        return service;
    }
}
