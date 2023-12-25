package com.ty.mid.framework.mybatisplus.service.cache.generic;

import com.ty.mid.framework.common.dto.AbstractDTO;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.core.Converter;
import com.ty.mid.framework.mybatisplus.service.cache.CacheService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.cache.Cache;
import javax.cache.CacheManager;
import java.io.Serializable;

/**
 * 抽象的带缓存service，实现自动缓存数据的功能
 * 1.默认缓存Map
 * 2.缓存使用JCache api
 *
 * @param <T>
 * @param <ID>
 * @param <D>
 */
@Slf4j
public abstract class AbstractCacheService<T extends BaseIdDO<ID>, ID extends Serializable, D extends AbstractDTO> implements CacheService<T, ID, D>, Converter<T, D> {

    @Resource(name = "jCacheCacheManager")
    protected CacheManager jCacheCacheManager;
    @Resource(name = "configuration")
    private javax.cache.configuration.Configuration<String, D> configuration;

    @PostConstruct
    public void init() {
        jCacheCacheManager.createCache(this.getCacheName(), configuration);
        log.info("initializing cache {}, cache class: {}", this.getCacheName(), getClass().getName());
        this.reloadCache();
    }

    @Override
    public Cache<String, D> getCache() {
        return jCacheCacheManager.getCache(getCacheName());
    }
}
