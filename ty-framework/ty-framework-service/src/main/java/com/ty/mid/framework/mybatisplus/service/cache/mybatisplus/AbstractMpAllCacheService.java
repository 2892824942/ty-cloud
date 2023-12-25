package com.ty.mid.framework.mybatisplus.service.cache.mybatisplus;

import com.ty.mid.framework.common.dto.AbstractDTO;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.mybatisplus.service.AbstractGenericService;
import com.ty.mid.framework.mybatisplus.service.cache.CacheService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.cache.Cache;
import javax.cache.CacheManager;
import java.io.Serializable;
import java.util.List;

/**
 * 抽象的带缓存service，基于mybatis-plus,实现自动缓存全量数据的功能
 * 注意:
 * 1.此类适合缓存如字典,全国市区等基本不变且数量不多的场景,如果经常改变或者数据量超过5000,请慎用此方法,这会影响系统启动速度
 * 2.如果不需要缓存全部,请使用AbstractCacheService 定制缓存的内容
 * <p>
 * 支持map格式缓存，支持list缓存
 *
 * @param <T>
 * @param <ID>
 * @param <M>
 * @param <D>
 */
@Slf4j
public abstract class AbstractMpAllCacheService<T extends BaseIdDO<ID>, ID extends Serializable, M extends BaseMapperX<T>, D extends AbstractDTO> extends AbstractGenericService<T, ID, M, D> implements CacheService<T, ID, D> {
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

    @Override
    public List<T> listFromDbNeedCache() {
        return super.list();
    }

}
