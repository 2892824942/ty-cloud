package com.ty.mid.framework.mybatisplus.service.wrapper;

import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.mybatisplus.service.AbstractGenericService;

import java.util.Collection;
import java.util.Map;

public abstract class MultiAutoWrapService<S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> extends AbstractGenericService<S, M> implements AutoWrapper<S, T, M> {

    protected final Class<?>[] typeArguments = GenericTypeUtils.resolveTypeArguments(this.getClass(), this.getClass());
    public abstract <DS> Map<DS, T> autoWrap(Collection<DS> collection);

    public Map<Long, T> idAutoWrap(Collection<Long> collection){
        return convert2IdMap(this.mapperClass, collection,GenericsUtil.cast2Class(typeArguments[1]) );
    }

}
