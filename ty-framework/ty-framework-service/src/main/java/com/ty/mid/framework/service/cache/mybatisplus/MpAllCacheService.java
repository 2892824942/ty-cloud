package com.ty.mid.framework.service.cache.mybatisplus;

import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.service.cache.generic.CacheService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 抽象的带缓存service，基于mybatis-plus,实现自动缓存全量数据的功能
 * 注意:
 * 1.此类适合缓存如字典,全国市区等基本不变且数量不多的场景,如果经常改变或者数据量超过5000,请慎用此方法,这会影响系统启动速度
 * 2.如果不需要缓存全部,请使用AbstractCacheService 定制缓存的内容
 * <p>
 * 支持map格式缓存，支持list缓存
 *
 * @param <S>
 * @param <T>
 * @param <M>
 */
@Slf4j
public abstract class MpAllCacheService<S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> extends CacheService<S, T, M> {

    @Override
    public List<S> listFromDbNeedCache() {
        return super.list();
    }

}
