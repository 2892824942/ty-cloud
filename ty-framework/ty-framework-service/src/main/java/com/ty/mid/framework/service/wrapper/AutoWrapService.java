package com.ty.mid.framework.service.wrapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.lang.NullSafe;
import com.ty.mid.framework.common.pojo.PageResult;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.core.util.MapstructUtils;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.service.GenericService;
import com.ty.mid.framework.service.wrapper.core.AutoWrapper;
import com.ty.mid.framework.service.wrapper.core.MappingProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class AutoWrapService<S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> extends GenericService<S, M> implements AutoWrapper<S, T> {

    protected final Class<?>[] targetGenericClasses = GenericTypeUtils.resolveTypeArguments(this.getClass(), AutoWrapService.class);


    @Override
    public Map<?, T> autoWrap(Collection<?> collection) {
        return idAutoWrap(GenericsUtil.cast(collection));
    }

    public Map<Long, T> idAutoWrap(Collection<Long> collection) {
        return convertMap(collection);
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

    public final <DS> Map<DS, T> convertMap(Collection<DS> collection) {
        Function<T, DS> tFunction = ds -> (DS) ds.getId();
        return convertMap(collection, S::getId, tFunction);
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
    public final <DS> Map<DS, T> convertMap(Collection<DS> collection, SFunction<S, ?> sFunction, Function<T, DS> tFunction) {

        return convert2Map(this.getBaseMapper(), sFunction, tFunction, collection);
    }

    private Class<T> getDtoClass() {
        return GenericsUtil.cast2Class(targetGenericClasses[1]);
    }

    private Class<S> getDoClass() {
        return GenericsUtil.cast2Class(targetGenericClasses[0]);
    }


    /***↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓mapstruct-plus自动转DTO↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓**/

    /**
     * 将DO实体的分页参数转换为目标DTO分页参数,DO实体的分页参数转换为目标DTO分页参数,
     * 与Service定义一致时使用
     * 具体使用按个人习惯
     *
     *
     * @param dataPage DO实体的分页参数
     * @return
     */
    public PageResult<T> covertPage(PageResult<S> dataPage) {
        return super.covertPage(dataPage, (t) -> convert(dataPage.getList(), getDtoClass()));
    }

    /**
     * 将DO实体的分页参数转换为目标DTO分页参数,与Service定义不一致时使用
     * BaseAutoConvert#covertPage(com.ty.mid.framework.common.pojo.PageResult, java.util.function.Function)
     * 具体使用按个人习惯
     *
     *
     * @param dataPage DO实体的分页参数
     * @return
     */
    public <DTO extends BaseIdDO<Long>> PageResult<DTO> covertPage(PageResult<S> dataPage, Class<DTO> dtoClass) {
        return super.covertPage(dataPage, (t) -> convert(dataPage.getList(), dtoClass));
    }

    public T selectOneDTO(String field, Object value) {
        return convert(super.selectOne(field, value));
    }


    public T selectOneDTO(String field, Object value, boolean throwEx) {
        return convert(super.selectOne(field, value, throwEx));
    }


    public T selectOneDTO(SFunction<S, ?> field, Object value) {
        return convert(super.selectOne(field, value));
    }

    public T selectOneDTO(SFunction<S, ?> field, Object value, boolean throwEx) {
        return convert(super.selectOne(field, value, throwEx));
    }

    public T selectOneDTO(String field1, Object value1, String field2, Object value2) {
        return convert(super.selectOne(field1, value1, field2, value2));
    }

    public T selectOneDTO(String field1, Object value1, String field2, Object value2, boolean throwEx) {
        return convert(super.selectOne(field1, value1, field2, value2, throwEx));
    }

    public T selectOneDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2) {
        return convert(super.selectOne(field1, value1, field2, value2));
    }

    public T selectOneDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2, boolean throwEx) {
        return convert(super.selectOne(field1, value1, field2, value2, throwEx));
    }

    public T selectOneDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2,
                          SFunction<S, ?> field3, Object value3) {
        return convert(super.selectOne(field1, value1, field2, value2, field3, value3));
    }

    public T selectOneDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2,
                          SFunction<S, ?> field3, Object value3, boolean throwEx) {
        return convert(super.selectOne(field1, value1, field2, value2, field3, value3, throwEx));
    }


    public List<T> selectListDTO() {
        return convert2DTO(super.selectList());
    }

    public Map<Long, T> selectMapDTO() {
        return IterUtil.toMap(selectListDTO(), BaseIdDO::getId);
    }


    public <K> Map<K, T> selectMapDTO(SFunction<T, K> keyField) {
        return IterUtil.toMap(selectListDTO(), keyField);
    }


    public List<T> selectListDTO(SFunction<S, ?> field, Object value) {
        return convert2DTO(super.selectList(field, value));
    }

    public Map<Long, T> selectMapDTO(SFunction<S, ?> field, Object value) {
        return IterUtil.toMap(selectListDTO(field, value), BaseIdDO::getId);
    }

    public <K> Map<K, T> selectMapDTO(SFunction<S, ?> field, Object value, SFunction<T, K> keyField) {
        return IterUtil.toMap(selectListDTO(field, value), keyField);

    }

    public List<T> selectListDTO(SFunction<S, ?> field, Collection<?> values) {
        return convert2DTO(super.selectList(field, values));
    }

    public Map<Long, T> selectMapDTO(SFunction<S, ?> field, Collection<?> values) {
        return IterUtil.toMap(selectListDTO(field, values), BaseIdDO::getId);
    }

    public <K> Map<K, T> selectMapDTO(SFunction<S, ?> field, Collection<?> values, SFunction<T, K> keyField) {
        return IterUtil.toMap(selectListDTO(field, values), keyField);
    }

    public List<T> selectListDTO(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2) {
        return convert2DTO(super.selectList(field1, value1, field2, value2));
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
     * @return desc
     */
    @NullSafe
    protected T convert(S source) {
        T target = convert(source, getDtoClass());
        if (Objects.isNull(target)) {
            return target;
        }
        MappingProvider.autoWrapper(source, target);
        return target;
    }


    /**
     * 将 S 类型对象，转换为 desc 类型的对象并返回
     *
     * @param source 数据来源实体
     * @param desc   描述对象 转换后的对象
     * @return desc
     */
    @NullSafe
    protected <TO> TO convert(S source, Class<TO> desc) {
        TO target = MapstructUtils.convert(source, desc);
        MappingProvider.afterAll(target);
        return target;
    }


    /**
     * 将 T 类型对象，按照配置的映射字段规则，给 desc 类型的对象赋值并返回 desc 对象
     *
     * @param source 数据来源实体
     * @param desc   转换后的对象
     * @return desc
     */
    @NullSafe
    protected T convert(S source, T desc) {
        T target = MapstructUtils.convert(source, desc);
        MappingProvider.autoWrapper(source, target);
        return target;
    }


    /**
     * 将 任意S类型对象，按照配置的映射字段规则，给 desc 类型的对象赋值并返回 desc 对象
     *
     * @param source 数据来源实体
     * @return desc
     */
    @NullSafe
    protected List<T> convert2DTO(List<S> source) {
        return convert(source, getDtoClass());
    }


    /**
     * 将 TO 类型对象，按照配置的映射字段规则，给 desc 类型的对象赋值并返回 desc 对象
     *
     * @param source 数据来源实体
     * @return desc
     */
    @NullSafe
    protected <TO> S convert(TO source) {
        return MapstructUtils.convert(source, getDoClass());
    }



    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓以下为非service定义DO<-->DTO转换,为了隐藏底层api,统一service层调用方式,增加以下api↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/


    /**
     * 将 T 类型对象，按照配置的映射字段规则，给 desc 类型的对象赋值并返回 desc 对象
     *
     * @param source 数据来源实体
     * @param desc   转换后的对象
     * @return desc
     */
    @NullSafe
    protected <SO, TO> TO convert(SO source, TO desc) {
        TO target = MapstructUtils.convert(source, desc);
        MappingProvider.afterAll(target);
        return target;
    }


    /**
     * 将 T 类型对象，按照配置的映射字段规则，给 desc 类型的对象赋值并返回 desc 对象
     *
     * @param source 数据来源实体
     * @param desc   转换后的对象
     * @return desc
     */
    @NullSafe
    protected <SO, TO> TO convert(SO source, Class<TO> desc) {
        TO target = MapstructUtils.convert(source, desc);
        MappingProvider.afterAll(target);
        return target;
    }


    /**
     * 将 SO 类型的集合，转换为 desc 类型的集合并返回
     *
     * @param sourceList 数据来源实体列表
     * @param desc       描述对象 转换后的对象
     * @return desc
     */
    @NullSafe
    protected <SO, TO> List<TO> convert(List<SO> sourceList, Class<TO> desc) {
        List<TO> target = MapstructUtils.convert(sourceList, desc);
        if (CollUtil.isEmpty(sourceList) || CollUtil.isEmpty(target)) {
            return target;
        }
        //数据库字段自动装配场景校验
        if (desc.isAssignableFrom(getDtoClass()) && sourceList.iterator().next().getClass().isAssignableFrom(getDoClass())) {
            //强制类型转化
            Collection<S> so = GenericsUtil.check2Collection(sourceList, getDoClass());
            Collection<T> to = GenericsUtil.check2Collection(target, getDtoClass());
            MappingProvider.autoWrapper(so, to);
        }
        MappingProvider.afterAll(target);
        return target;
    }


}
