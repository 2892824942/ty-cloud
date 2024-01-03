package com.ty.mid.framework.mybatisplus.service.wrapper;

import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;

import java.util.Collection;
import java.util.Map;

/**
 * 可缓存service的定义
 *
 * @param <T>
 */
public abstract class AbstractAutoWrapper<DS, S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> implements AutoWrapper<S, T, M> {

    public abstract Map<DS, T> autoWrap(Collection<DS> collection);

}
