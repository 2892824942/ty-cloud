package com.ty.mid.framework.service.wrapper.core;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.util.JsonUtils;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 可缓存service的定义
 *
 * @param <T>
 */
public interface AutoWrapper<S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> {

    Map<?, T> autoWrap(Collection<?> collection);

    /**
     * 1.使用BeanUtils 复制完成S->T的映射
     * 2.使用Id查询并使用Id作为返回Map的Key
     * 适用于简单字段复制
     *
     * @param baseMapperXClass
     * @param collection
     * @return
     */
    @SuppressWarnings("unchecked")
    default <DS> Map<DS, T> convert2IdMap(Class<M> baseMapperXClass, Collection<DS> collection, Class<T> instanceClass) {
        Function<T, DS> tFunction = ds -> (DS) ds.getId();
        return convert2Map(baseMapperXClass, S::getId, tFunction, collection, instanceClass);
    }

    /**
     * 1.使用Id查询并使用Id作为返回Map的Key
     * 适用于简单字段复制
     *
     * @param baseMapperXClass
     * @param collection
     * @return
     */
    @SuppressWarnings("unchecked")
    default <DS> Map<DS, T> convert2IdMap(Class<M> baseMapperXClass, Collection<DS> collection, Function<List<S>, List<T>> function) {
        Function<T, DS> tFunction = ds -> (DS) ds.getId();
        return convert2Map(baseMapperXClass, S::getId, tFunction, collection, function);
    }

    /**
     * 使用BeanUtils 复制完成S->T的映射
     * 适用于简单字段复制
     *
     * @param baseMapperXClass
     * @param sFunction
     * @param tFunction
     * @param collection
     * @return
     */
    default <DS> Map<DS, T> convert2Map(Class<M> baseMapperXClass, SFunction<S, ?> sFunction, Function<T, DS> tFunction, Collection<DS> collection, Class<T> instanceClass) {
        List<S> mDo = SpringContextHelper.getBean(baseMapperXClass).selectList(sFunction, collection);
        List<T> targetList = JsonUtils.parseArray(JsonUtils.toJson(mDo), instanceClass);
        if (CollUtil.isEmpty(targetList)) {
            return Collections.emptyMap();
        }
        return targetList.stream().collect(Collectors.toMap(tFunction, Function.identity(), (a, b) -> a));
    }

    /**
     * 使用给定的Function<S, T> function 完成复制
     *
     * @param baseMapperXClass
     * @param sFunction
     * @param tFunction
     * @param collection
     * @return
     */
    default <DS> Map<DS, T> convert2Map(Class<M> baseMapperXClass, SFunction<S, ?> sFunction, Function<T, DS> tFunction, Collection<DS> collection, Function<List<S>, List<T>> function) {
        List<S> mDo = SpringContextHelper.getBean(baseMapperXClass).selectList(sFunction, collection);
        List<T> targetList = function.apply(mDo);
        if (CollUtil.isEmpty(targetList)) {
            return Collections.emptyMap();
        }
        return targetList.stream().collect(Collectors.toMap(tFunction, Function.identity(), (a, b) -> a));
    }
}