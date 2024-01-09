package com.ty.mid.framework.service.cache.generic;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.service.cache.jcache.listener.CacheListener;
import com.ty.mid.framework.service.cache.jcache.listener.through.ReadThroughLoader;
import com.ty.mid.framework.service.integrate.GenericAutoWrapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.jcache.JCacheCacheManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * 抽象的带缓存service，实现自动缓存数据的功能
 * 1.默认缓存Map
 * 2.缓存使用JCache api
 *
 * @param <T>
 * @see CacheService#cacheManager
 * 3.缓存key默认为实体类的主键,需要更改重写
 * @see CacheService#defineMapKey()
 */
@Slf4j
public abstract class CacheService<S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> extends GenericAutoWrapService<S, T, M> implements BaseCacheService<S, T> {

    @Resource
    protected JCacheCacheManager cacheManager;
    @Resource
    private javax.cache.configuration.Configuration<String, T> configuration;

    protected CacheManager getCacheManager() {
        return cacheManager.getCacheManager();
    }


    @PostConstruct
    public void init() {
        if (configuration instanceof MutableConfiguration) {
            MutableConfiguration mutableConfiguration = (MutableConfiguration) configuration;
            ReadThroughLoader<String, T> readThroughLoader = new ReadThroughLoader<>();
            readThroughLoader.setCacheService(this);
            mutableConfiguration.setCacheLoaderFactory(FactoryBuilder.factoryOf(readThroughLoader));
        }

        Cache<String, T> cache = getCacheManager().createCache(this.getCacheName(), configuration);
        MutableCacheEntryListenerConfiguration<String, T> mutableCacheEntryListenerConfiguration = new MutableCacheEntryListenerConfiguration<String, T>(
                FactoryBuilder.factoryOf(GenericsUtil.cast2Class(CacheListener.class)), null, false, false
        );

        cache.registerCacheEntryListener(mutableCacheEntryListenerConfiguration);
        log.info("initializing cache {}, cache class: {}", this.getCacheName(), getClass().getName());
        this.reloadCache();
    }

    @Override
    public Cache<String, T> getCache() {
        return getCacheManager().getCache(getCacheName());
    }

    @Override
    public Function<T, String> defineMapKey() {
        return a -> String.valueOf(a.getId());
    }

    @Override
    public SFunction<S, ?> defineSourceMapKey() {
        return S::getId;
    }

    @Override
    public List<S> listFromDbNeedCache(Iterator<String> keys) {
        List<String> keyList = new ArrayList<>();
        keys.forEachRemaining(keyList::add);
        return this.selectList(defineSourceMapKey(), keyList);
    }

    @Override
    public S getDataFromDbNeedCache(String key) {
        return this.selectOne(defineSourceMapKey(), key);
    }


}
