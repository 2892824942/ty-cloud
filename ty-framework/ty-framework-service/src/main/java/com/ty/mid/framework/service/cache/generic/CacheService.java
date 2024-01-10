package com.ty.mid.framework.service.cache.generic;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.common.util.SafeGetUtil;
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
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;

/**
 * 抽象的带缓存service，实现自动缓存数据的功能
 * 1.默认缓存Map
 * 2.缓存使用JCache api
 *
 * @param <T>
 * @see CacheService#cacheManager
 * 3.缓存key默认为实体类的主键,需要更改重写
 * @see CacheService#cacheDefineDTOMapKey()
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
    @SuppressWarnings("unchecked")
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
        this.cacheReload();
    }

    @Override
    public Cache<String, T> getCache() {
        return getCacheManager().getCache(getCacheName());
    }

    @Override
    public String getCacheName() {
        return this.getClass().getName();
    }

    @Override
    public SFunction<S, ?> cacheDefineDOMapKey() {
        return S::getId;
    }

    @Override
    public @NotNull String cacheDelimiter() {
        return ",";
    }

    @Override
    public List<SFunction<S, ?>> cacheDefineDOMapKeys() {
        return Collections.emptyList();
    }


    @Override
    public List<S> cacheLoadListFromDb(Iterator<String> keys) {
        List<String> keyList = new ArrayList<>();
        keys.forEachRemaining(keyList::add);
        return getDbList(keyList);
    }

    @Override
    public S cacheLoadFromDb(String key) {
        List<S> dbList = getDbList(Collections.singletonList(key));
        if (CollUtil.isEmpty(dbList)){
            return null;
        }
        return dbList.get(0);
    }

    private List<S> getDbList(List<String> keyList) {
        if (isMultiColumn()) {
            LambdaQueryWrapper<S> wrapper = new LambdaQueryWrapper<>();
            List<SFunction<S, ?>> sFunctions = cacheDefineDOMapKeys();
            List<List<String>> lists = parseKeyStr(keyList);
            for (int i = 0; i < sFunctions.size() && i < lists.size(); i++) {
                Collection<String> subKeys = lists.get(i);
                if (CollUtil.isEmpty(subKeys)) {
                    break;
                }
                SFunction<S, ?> sFunction = sFunctions.get(i);
                wrapper.in(sFunction, subKeys);
            }
            return baseMapper.selectList(wrapper);
        }
        return this.selectList(cacheDefineDOMapKey(), keyList);
    }


}
