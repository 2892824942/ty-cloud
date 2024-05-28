package com.ty.mid.framework.service.cache.generic;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.service.cache.jcache.listener.CacheListener;
import com.ty.mid.framework.service.cache.jcache.listener.through.ReadThroughLoader;
import com.ty.mid.framework.service.wrapper.AutoWrapService;
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
import java.util.stream.Collectors;

/**
 * 一:抽象的带缓存service，实现自动缓存数据的功能 <p>
 * 1.缓存Map对象 <p>
 * 2.缓存使用JCache api,支持key集合查询及删除 <p>
 *
 * @link com.ty.mid.framework.service.cache.support.DefaultServiceCacheLoader <p>
 * -如果缓存相关业务需要强一致性,切勿使用此方式,参考缓存模块-Redis2PCCacheManager <p>
 * @see CacheAutoWrapService#jCacheCacheManager <p>
 * 3.缓存key默认为实体类的主键,需要更改为其他字段重写CacheService#cacheDefineDOMapKey(),如果需要多个属性作为主键,重写CacheService#cacheDefineDOMapKeys() <p>
 * 4.默认开启Read-Through模式(一个实现,一个定义),如果缓存中不存在数据,会自动从数据库加载数据.---暂时不支持存储null,且没有直接提供缓存穿透场景默认支持(后续再说) <p>
 * 5.默认不开启Write-Through模式,即增删改操作需要开发者自己维护缓存一致性 <p>
 * 6.缓存失效时间如果不设置为永不过期,,高QPS场景需要根据失效时间的时长,考虑缓存击穿, <p>
 * 7.默认开启Cache-Listener模式(所有JCache缓存共用),即缓存数据发生变化时,会自动触发监听器 <p>
 * 使用说明: <p>
 * -如果缓存相关业务需要缓存即刻生效,需要手动干预缓存(建议直接删除)已达到增删改后自动更新缓存的目的 <p>
 * -如果缓存相关业务不需要即刻生效,可设置缓存更新时间或者通过cache加载管理器统一处理 <p>
 * 二:集成了自动装载Service
 */
@Slf4j
public abstract class CacheAutoWrapService<S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> extends AutoWrapService<S, T, M> implements BaseCacheService<S, T> {

    @Resource
    protected JCacheCacheManager jCacheCacheManager;
    @Resource
    private javax.cache.configuration.Configuration<String, T> configuration;

    protected CacheManager getjCacheCacheManager() {
        return jCacheCacheManager.getCacheManager();
    }


    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        if (configuration instanceof MutableConfiguration) {
            MutableConfiguration mutableConfiguration = (MutableConfiguration) configuration;
            ReadThroughLoader<String, T> readThroughLoader = new ReadThroughLoader<>();
            readThroughLoader.setCacheAutoWrapService(this);
            mutableConfiguration.setCacheLoaderFactory(FactoryBuilder.factoryOf(readThroughLoader));
        }

        Cache<String, T> cache = getjCacheCacheManager().createCache(this.getCacheName(), configuration);
        MutableCacheEntryListenerConfiguration<String, T> mutableCacheEntryListenerConfiguration = new MutableCacheEntryListenerConfiguration<String, T>(
                FactoryBuilder.factoryOf(GenericsUtil.cast2Class(CacheListener.class)), null, false, false
        );

        cache.registerCacheEntryListener(mutableCacheEntryListenerConfiguration);
        log.info("initializing cache {}, cache class: {}", this.getCacheName(), getClass().getName());
    }

    @Override
    public Cache<String, T> getCache() {
        return getjCacheCacheManager().getCache(getCacheName());
    }

    @Override
    public String getCacheName() {
        return this.getClass().getName();
    }

    /**
     * 默认id作为key
     *
     * @return
     */


    @Override
    public SFunction<S, ?> cacheDefineDOMapKey() {
        return S::getId;
    }

    /**
     * 多个实体属性拼接key的场景,拼接使用的间隔符定义
     *
     * @return
     */
    @Override
    public @NotNull String cacheDelimiter() {
        return ",";
    }

    /**
     * 默认不采用多个属性数据拼接
     * 如重写将覆盖
     *
     * @return
     * @link com.ty.mid.framework.service.cache.generic.CacheService#cacheDefineDOMapKey()
     */

    @Override
    public List<SFunction<S, ?>> cacheDefineDOMapKeys() {
        return Collections.emptyList();
    }


    @Override
    public List<S> cacheLoadListFromDb(Collection<String> keys) {
        return getDbList(new ArrayList<>(keys));
    }

    @Override
    public S cacheLoadFromDb(String key) {
        List<S> dbList = getDbList(Collections.singletonList(key));
        if (CollUtil.isEmpty(dbList)) {
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

    private List<List<String>> parseKeyStr(List<String> keys) {
        if (isMultiColumn()) {
            List<List<String>> dataList = cacheDefineDTOMapKeys().stream().map(function -> new ArrayList<String>()).collect(Collectors.toList());
            //多个参数定义
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String[] split = key.split(cacheDelimiter());
                List<String> collect = Arrays.stream(split).collect(Collectors.toList());
                for (int j = 0; i < collect.size(); i++) {
                    dataList.get(j).add(collect.get(j));
                }

            }
            return dataList;
        }
        //单一定义
        return Collections.singletonList(keys);
    }

    @Override
    public <DS, M extends BaseMapperX<S, Long>> Collection<T> getTargetList(M baseMapperX, SFunction<S, ?> sFunction, Collection<DS> collection) {
        //当前的Service拥有缓存和自动装载两个能力
        SFunction<S, ?> cacheSFunction = cacheDefineDOMapKey();
        //对比是否是同一属性
        if (Objects.equals(LambdaUtils.extract(cacheSFunction).getImplMethodName(), LambdaUtils.extract(sFunction).getImplMethodName())) {
            //如果是则走缓存
            Collection<String> keys = GenericsUtil.check2Collection(collection);
            Map<String, T> all = GenericsUtil.check2Map(getCache().getAll(new HashSet<>(keys)));
            return all.values();
        }
        return super.getTargetList(baseMapperX, sFunction, collection);
    }
}
