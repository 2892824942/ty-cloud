package com.ty.mid.framework.mybatisplus.service.support;

import com.ty.mid.framework.common.dto.AbstractDTO;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.core.config.ApplicationConfiguration;
import com.ty.mid.framework.mybatisplus.service.ServiceCacheLoader;
import com.ty.mid.framework.mybatisplus.service.cache.generic.BaseCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * cache加载器
 * 用于获取全局的基于CacheService实现的所有的缓存
 * 用途:
 * 1.可统一处理缓存过期刷新逻辑,比如使用job跑批更新缓存或删除缓存
 */
@Slf4j
public class DefaultServiceCacheLoader implements ServiceCacheLoader {

    @Autowired
    private ApplicationConfiguration configuration;

    @Autowired(required = false)
    private List<BaseCacheService<? extends BaseIdDO<?>, ? extends AbstractDTO>> cacheServices = new ArrayList<>();

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
        this.resolveService(cacheName).reloadCache();
        log.info("successfully reload cache: {}", cacheName);
    }

    @Override
    public void clearCache(String cacheName) {
        this.resolveService(cacheName).clearCache();
        log.info("successfully clear cache: {}", cacheName);
    }

    protected BaseCacheService<? extends BaseIdDO<?>, ? extends AbstractDTO> resolveService(String cacheName) {
        BaseCacheService<? extends BaseIdDO<?>, ? extends AbstractDTO> service = cacheServices.stream().filter(c -> c.getCacheName().equals(cacheName)).findFirst().orElse(null);
        Validator.requireNonNull(cacheName, Validator.formatMessage("缓存 [%s] 不存在", cacheName));
        return service;
    }
}
