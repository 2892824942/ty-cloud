package com.ty.mid.framework.service.wrapper;

import cn.hutool.core.collection.IterUtil;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.common.util.JsonUtils;
import com.ty.mid.framework.core.util.MapstructUtils;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.service.GenericService;
import com.ty.mid.framework.service.wrapper.core.AutoWrapper;
import com.ty.mid.framework.service.wrapper.core.MappingProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AutoWrapService<S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> extends GenericService<S, M> implements AutoWrapper<S,T> {

    protected final Class<?>[] targetGenericClasses = GenericTypeUtils.resolveTypeArguments(this.getClass(), AutoWrapService.class);


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
     * 使用BeanUtils 复制完成S->T的映射
     * 适用于简单字段复制
     *
     * @param sFunction
     * @param tFunction
     * @param collection
     * @return
     */
    public final <DS> Map<DS, T> convert(Collection<DS> collection , SFunction<S, ?> sFunction, Function<T, DS> tFunction) {

        return convert2Map(this.getBaseMapper(), sFunction, tFunction,collection);
    }

    private Class<T> getDtoClass() {
        return GenericsUtil.cast2Class(targetGenericClasses[1]);
    }



    /***↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓mapstruct-plus自动转DTO↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓**/

    public T selectOneDTO(String field, Object value) {
        return convert(super.selectOne(field, value), getDtoClass());
    }


    public T selectOneDTO(String field, Object value, boolean throwEx) {
        return convert(super.selectOne(field, value, throwEx), getDtoClass());
    }


    public T selectOneDTO(SFunction<S, ?> field, Object value) {
        return convert(super.selectOne(field, value), getDtoClass());
    }

    public T selectOneDTO(SFunction<S, ?> field, Object value, boolean throwEx) {
        return convert(super.selectOne(field, value, throwEx), getDtoClass());
    }

    public T selectOneDTO(String field1, Object value1, String field2, Object value2) {
        return convert(super.selectOne(field1, value1, field2, value2), getDtoClass());
    }

    public T selectOneDTO(String field1, Object value1, String field2, Object value2, boolean throwEx) {
        return convert(super.selectOne(field1, value1, field2, value2, throwEx), getDtoClass());
    }

    public T selectOneDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2) {
        return convert(super.selectOne(field1, value1, field2, value2), getDtoClass());
    }

    public T selectOneDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2, boolean throwEx) {
        return convert(super.selectOne(field1, value1, field2, value2, throwEx), getDtoClass());
    }

    public T selectOneDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2,
                          SFunction<S, ?> field3, Object value3) {
        return convert(super.selectOne(field1, value1, field2, value2, field3, value3), getDtoClass());
    }

    public T selectOneDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2,
                          SFunction<S, ?> field3, Object value3, boolean throwEx) {
        return convert(super.selectOne(field1, value1, field2, value2, field3, value3, throwEx), getDtoClass());
    }


    public List<T> selectListDTO() {
        return convert(super.selectList(), getDtoClass());
    }

    public Map<Long, T> selectMapDTO() {
        return IterUtil.toMap(selectListDTO(), BaseIdDO::getId);
    }


    public <K> Map<K, T> selectMapDTO(SFunction<T, K> keyField) {
        return IterUtil.toMap(selectListDTO(), keyField);
    }


    public List<T> selectListDTO(SFunction<S, ?> field, Object value) {
        return convert(super.selectList(field, value), getDtoClass());
    }

    public Map<Long, T> selectMapDTO(SFunction<S, ?> field, Object value) {
        return IterUtil.toMap(selectListDTO(field, value), BaseIdDO::getId);
    }

    public <K> Map<K, T> selectMapDTO(SFunction<S, ?> field, Object value, SFunction<T, K> keyField) {
        return IterUtil.toMap(selectListDTO(field, value), keyField);

    }

    public List<T> selectListDTO(SFunction<S, ?> field, Collection<?> values) {
        return convert(super.selectList(field, values), getDtoClass());
    }

    public Map<Long, T> selectMapDTO(SFunction<S, ?> field, Collection<?> values) {
        return IterUtil.toMap(selectListDTO(field, values), BaseIdDO::getId);
    }

    public <K> Map<K, T> selectMapDTO(SFunction<S, ?> field, Collection<?> values, SFunction<T, K> keyField) {
        return IterUtil.toMap(selectListDTO(field, values), keyField);
    }

    public List<T> selectListDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2) {
        return convert(super.selectList(field1, value1, field2, value2), getDtoClass());
    }

    public Map<Long, T> selectMapDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2) {
        return IterUtil.toMap(selectListDTO(field1, value1, field2, value2), BaseIdDO::getId);
    }

    public <K> Map<K, T> selectMapDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2, SFunction<T, K> keyField) {
        return IterUtil.toMap(selectListDTO(field1, value1, field2, value2), keyField);
    }

    /**
     * 将 T 类型对象，转换为 desc 类型的对象并返回
     *
     * @param source 数据来源实体
     * @param desc   描述对象 转换后的对象
     * @return desc
     */
    private T convert(S source, Class<T> desc) {
        T target = MapstructUtils.convert(source, desc);
        MappingProvider.autoWrapper(source, target);
        return target;
    }

    /**
     * 将 T 类型对象，按照配置的映射字段规则，给 desc 类型的对象赋值并返回 desc 对象
     *
     * @param source 数据来源实体
     * @param desc   转换后的对象
     * @return desc
     */
    private T convert(S source, T desc) {
        T target = MapstructUtils.convert(source, desc);
        MappingProvider.autoWrapper(source, target);
        return target;
    }

    /**
     * 将 T 类型的集合，转换为 desc 类型的集合并返回
     *
     * @param sourceList 数据来源实体列表
     * @param desc       描述对象 转换后的对象
     * @return desc
     */
    private List<T> convert(List<S> sourceList, Class<T> desc) {
        List<T> target = MapstructUtils.convert(sourceList, desc);
        MappingProvider.autoWrapper(sourceList, target);
        return target;
    }

}
