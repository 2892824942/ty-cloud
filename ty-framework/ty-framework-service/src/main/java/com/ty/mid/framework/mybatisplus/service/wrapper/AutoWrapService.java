package com.ty.mid.framework.mybatisplus.service.wrapper;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.common.util.JsonUtils;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.mybatisplus.service.AbstractGenericService;
import com.ty.mid.framework.mybatisplus.service.wrapper.core.AutoWrapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AutoWrapService<S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> extends AbstractGenericService<S, M> implements AutoWrapper<S, T, M> {

    protected final Class<?> targetGenericClass = GenericsUtil.getGenericTypeByIndex(this.getClass(), 1);

    @Override
    public Map<?, T> autoWrap(Collection<?> collection) {

        return idAutoWrap(GenericsUtil.cast(collection));
    }

    public Map<Long, T> idAutoWrap(Collection<Long> collection) {
        return convert(collection);
    }

    /**
     * 1.使用BeanUtils 复制完成S->T的映射
     * 2.使用Id查询并使用Id作为返回Map的Key
     * 适用于简单字段复制
     *
     * @param collection
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <DS> Map<DS, T> convert(Collection<DS> collection) {
        Function<T, DS> tFunction = ds -> (DS) ds.getId();
        return convert(collection, S::getId, tFunction);
    }


    /**
     * 1.使用Id查询并使用Id作为返回Map的Key
     * 适用于简单字段复制
     *
     * @param collection
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <DS> Map<DS, T> convert(Collection<DS> collection, Function<List<S>, List<T>> function) {
        Function<T, DS> tFunction = ds -> (DS) ds.getId();
        return convert(collection, S::getId, tFunction, function);
    }

    /**
     * 使用BeanUtils 复制完成S->T的映射
     * 适用于简单字段复制
     *
     * @param sFunction
     * @param tFunction
     * @param collection
     * @return
     */
    public final <DS> Map<DS, T> convert(Collection<DS> collection, SFunction<S, ?> sFunction, Function<T, DS> tFunction) {
        Function<List<S>, List<T>> targetFunction = target -> JsonUtils.parseArray(JsonUtils.toJson(target), GenericsUtil.cast2Class(targetGenericClass));

        return convert(collection, sFunction, tFunction, targetFunction);
    }


    /**
     * 使用给定的Function<S, T> function 完成复制
     *
     * @param sFunction
     * @param tFunction
     * @param collection
     * @return
     */
    public final <DS> Map<DS, T> convert(Collection<DS> collection, SFunction<S, ?> sFunction, Function<T, DS> tFunction, Function<List<S>, List<T>> function) {
        List<S> mDo = this.getBaseMapper().selectList(sFunction, collection);
        List<T> targetList = function.apply(mDo);
        if (CollUtil.isEmpty(targetList)) {
            return Collections.emptyMap();
        }
        return targetList.stream().collect(Collectors.toMap(tFunction, Function.identity(), (a, b) -> a));
    }

}
