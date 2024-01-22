package com.ty.mid.framework.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ty.mid.framework.common.pojo.PageResult;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class GenericService<T extends BaseDO, M extends BaseMapperX<T, Long>> extends ServiceImpl<M, T> implements IService<T> {

    /**
     * id 是否空
     * <p>
     * id != null 且  id != 0
     *
     * @param id
     * @return
     */
    public boolean isIdNull(Long id) {
        return id == null || id.equals(0L);
    }

    /**
     * id 是为正数
     * <p>
     * id != null && id > 0L
     *
     * @param id
     * @return
     */
    public boolean isPositive(Long id) {
        return id != null && id > 0L;
    }

    /**
     * id 是为负数或空
     * <p>
     * id == null || id < 0L
     *
     * @param id
     * @return
     */
    public boolean isNegative(Long id) {
        return id == null || id < 0L;
    }


    /**
     * 将DO实体的分页参数转换为目标DTO分页参数,与BaseAutoConvert一致
     * BaseAutoConvert#covertPage(com.ty.mid.framework.common.pojo.PageResult, java.util.function.Function)
     * 具体使用按个人习惯
     * <p>
     *
     * @param dataPage DO实体的分页参数
     * @param function 转换方法
     * @return
     */
    public <S,T> PageResult<T> covertPage(PageResult<S> dataPage, Function<List<S>,List<T>> function) {
        if (CollectionUtil.isEmpty(dataPage.getList())) {
            return PageResult.empty();
        }
        List<T> resultPage = function.apply(dataPage.getList());
        return PageResult.of(resultPage, dataPage.getTotal());
    }

    public T selectOne(String field, Object value) {
        return baseMapper.selectOne(field, value);
    }

    public T selectOne(String field, Object value, boolean throwEx) {
        return baseMapper.selectOne(field, value, throwEx);
    }


    public T selectOne(SFunction<T, ?> field, Object value) {
        return baseMapper.selectOne(field, value);
    }

    public T selectOne(SFunction<T, ?> field, Object value, boolean throwEx) {
        return baseMapper.selectOne(field, value, throwEx);
    }

    public T selectOne(String field1, Object value1, String field2, Object value2) {
        return baseMapper.selectOne(field1, value1, field2, value2);
    }

    public T selectOne(String field1, Object value1, String field2, Object value2, boolean throwEx) {
        return baseMapper.selectOne(field1, value1, field2, value2, throwEx);
    }

    public T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return baseMapper.selectOne(field1, value1, field2, value2);
    }

    public T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2, boolean throwEx) {
        return baseMapper.selectOne(field1, value1, field2, value2, throwEx);
    }

    public T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2,
                       SFunction<T, ?> field3, Object value3) {
        return baseMapper.selectOne(field1, value1, field2, value2, field3, value3);
    }

    public T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2,
                       SFunction<T, ?> field3, Object value3, boolean throwEx) {
        return baseMapper.selectOne(field1, value1, field2, value2, field3, value3, throwEx);
    }

    public Long selectCount() {
        return baseMapper.selectCount();
    }

    public Long selectCount(String field, Object value) {
        return baseMapper.selectCount(field, value);
    }

    public Long selectCount(SFunction<T, ?> field, Object value) {
        return baseMapper.selectCount(field, value);
    }

    public List<T> selectList() {
        return baseMapper.selectList();
    }

    public Map<Long, T> selectMap() {
        return baseMapper.selectMap();
    }


    public <K> Map<K, T> selectMap(SFunction<T, K> keyField) {
        return baseMapper.selectMap(keyField);
    }


    public List<T> selectList(SFunction<T, ?> field, Object value) {
        return baseMapper.selectList(field, value);
    }

    public Map<Long, T> selectMap(SFunction<T, ?> field, Object value) {
        return baseMapper.selectMap(field, value);
    }

    public <K> Map<K, T> selectMap(SFunction<T, ?> field, Object value, SFunction<T, K> keyField) {
        return baseMapper.selectMap(field, value, keyField);
    }

    public List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        return baseMapper.selectList(field, values);
    }

    public Map<Long, T> selectMap(SFunction<T, ?> field, Collection<?> values) {
        return baseMapper.selectMap(field, values);
    }

    public <K> Map<K, T> selectMap(SFunction<T, ?> field, Collection<?> values, SFunction<T, K> keyField) {
        return baseMapper.selectMap(field, values, keyField);
    }

    public List<T> selectList(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return baseMapper.selectList(field1, value1, field2, value2);
    }

    public Map<Long, T> selectMap(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return baseMapper.selectMap(field1, value1, field2, value2);
    }

    public <K> Map<K, T> selectMap(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2, SFunction<T, K> keyField) {
        return baseMapper.selectMap(field1, value1, field2, value2, keyField);
    }

}
