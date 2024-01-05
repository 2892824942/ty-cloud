package com.ty.mid.framework.mybatisplus.service.cache.generic;

import com.ty.mid.framework.common.dto.AbstractDTO;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.mybatisplus.service.integrate.GenericAutoWrapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.jcache.JCacheCacheManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.cache.Cache;
import javax.cache.CacheManager;
import java.io.Serializable;
import java.util.function.Function;

/**
 * 抽象的带缓存service，实现自动缓存数据的功能
 * 1.默认缓存Map
 * 2.缓存使用JCache api
 * @see CacheService#cacheManager
 * 3.缓存key默认为实体类的主键,需要更改重写
 * @see CacheService#defineMapKey()
 *
 * @param <T>

 */
@Slf4j
public abstract class CacheService<S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> extends GenericAutoWrapService<S,T,M> implements BaseCacheService<S, T> {

    @Resource
    protected JCacheCacheManager cacheManager;
    @Resource
    private javax.cache.configuration.Configuration<String, T> configuration;

    protected CacheManager getCacheManager() {
        return cacheManager.getCacheManager();
    }

    @PostConstruct
    public void init() {
        getCacheManager().createCache(this.getCacheName(), configuration);
        log.info("initializing cache {}, cache class: {}", this.getCacheName(), getClass().getName());
        this.reloadCache();
    }

    @Override
    public Cache<String, T> getCache() {
        return getCacheManager().getCache(getCacheName());
    }

    @Override
    public Function<T, String> defineMapKey() {
        return a-> String.valueOf(a.getId());
    }
}
