package com.ty.mid.framework.mybatisplus.core.mapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.base.MPJBaseMapper;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.pojo.PageParam;
import com.ty.mid.framework.common.pojo.PageResult;
import com.ty.mid.framework.mybatisplus.core.util.MyBatisUtils;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 在 MyBatis Plus 的 BaseMapper 的基础上拓展，提供更多的能力
 * <p>
 * 1. {@link BaseMapper} 为 MyBatis Plus 的基础接口，提供基础的 CRUD 能力
 * 2. {@link MPJBaseMapper} 为 MyBatis Plus Join 的基础接口，提供连表 Join 能力
 * 3.为 MyBatis Plus 的List接口，快速转换Map能力
 */
public interface BaseMapperX<S extends BaseIdDO<ID>, ID extends Serializable> extends BaseMapper<S> {
    /**
     * 普通分页
     *
     * @param pageParam
     * @param queryWrapper
     * @return
     */
    default PageResult<S> selectPage(PageParam pageParam, @Param("ew") Wrapper<S> queryWrapper) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageNo())) {
            List<S> list = selectList(queryWrapper);
            return PageResult.of(list, (long) list.size());
        }

        // MyBatis Plus 查询
        IPage<S> mpPage = MyBatisUtils.buildPage(pageParam);
        selectPage(mpPage, queryWrapper);
        // 转换返回
        return PageResult.of(mpPage.getRecords(), mpPage.getTotal());
    }

    default S selectOne(String field, Object value) {
        return selectOne(field, value, Boolean.FALSE);
    }

    default S selectOne(String field, Object value, boolean throwEx) {
        return selectOne(new QueryWrapper<S>().eq(field, value), throwEx);
    }

    default S selectOne(SFunction<S, ?> field, Object value) {
        return selectOne(field, value, Boolean.FALSE);
    }

    default S selectOne(SFunction<S, ?> field, Object value, boolean throwEx) {
        return selectOne(new LambdaQueryWrapper<S>().eq(field, value), throwEx);
    }

    default S selectOne(String field1, Object value1, String field2, Object value2) {
        return selectOne(field1, value1, field2, value2, Boolean.FALSE);
    }

    default S selectOne(String field1, Object value1, String field2, Object value2, boolean throwEx) {
        return selectOne(new QueryWrapper<S>().eq(field1, value1).eq(field2, value2), throwEx);
    }

    default S selectOne(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2) {
        return selectOne(field1, value1, field2, value2, Boolean.FALSE);
    }

    default S selectOne(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2, boolean throwEx) {
        return selectOne(new LambdaQueryWrapper<S>().eq(field1, value1).eq(field2, value2), throwEx);
    }

    default S selectOne(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2,
                        SFunction<S, ?> field3, Object value3) {
        return selectOne(field1, value1, field2, value2, field3, value3, Boolean.FALSE);
    }


    default S selectOne(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2,
                        SFunction<S, ?> field3, Object value3, Boolean throwEx) {
        return selectOne(new LambdaQueryWrapper<S>().eq(field1, value1).eq(field2, value2)
                .eq(field3, value3), throwEx);
    }

    default Long selectCount() {
        return selectCount(new QueryWrapper<>());
    }

    default Long selectCount(String field, Object value) {
        return selectCount(new QueryWrapper<S>().eq(field, value));
    }

    default Long selectCount(SFunction<S, ?> field, Object value) {
        return selectCount(new LambdaQueryWrapper<S>().eq(field, value));
    }

    default List<S> selectList() {
        return selectList(new QueryWrapper<>());
    }


    default Map<ID, S> selectMap() {
        return IterUtil.toMap(selectList(), BaseIdDO::getId);
    }


    default <K> Map<K, S> selectMap(SFunction<S, K> keyField) {
        return IterUtil.toMap(selectList(), keyField);
    }


    default List<S> selectList(SFunction<S, ?> field, Object value) {
        return selectList(new LambdaQueryWrapper<S>().eq(field, value));
    }

    default Map<ID, S> selectMap(SFunction<S, ?> field, Object value) {
        return IterUtil.toMap(selectList(field, value), BaseIdDO::getId);
    }

    default <K> Map<K, S> selectMap(SFunction<S, ?> field, Object value, SFunction<S, K> keyField) {
        return IterUtil.toMap(selectList(field, value), keyField);
    }

    default List<S> selectList(SFunction<S, ?> field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return CollUtil.newArrayList();
        }
        return selectList(new LambdaQueryWrapper<S>().in(field, values));
    }

    default Map<ID, S> selectMap(SFunction<S, ?> field, Collection<?> values) {
        return IterUtil.toMap(selectList(field, values), BaseIdDO::getId);
    }

    default <K> Map<K, S> selectMap(SFunction<S, ?> field, Collection<?> values, SFunction<S, K> keyField) {
        return IterUtil.toMap(selectList(field, values), keyField);
    }

    default List<S> selectList(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2) {
        return selectList(new LambdaQueryWrapper<S>().eq(field1, value1).eq(field2, value2));
    }

    default Map<ID, S> selectMap(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2) {
        return IterUtil.toMap(selectList(field1, value1, field2, value2), BaseIdDO::getId);
    }

    default <K> Map<K, S> selectMap(SFunction<S, ?> field1, Object value1, SFunction<S, ?> field2, Object value2, SFunction<S, K> keyField) {
        return IterUtil.toMap(selectList(field1, value1, field2, value2), keyField);
    }

    /**
     * 批量插入，适合大量数据插入
     *
     * @param entities 实体们
     */
    default void insertBatch(Collection<S> entities) {
        Db.saveBatch(entities);
    }

    /**
     * 批量插入，适合大量数据插入
     *
     * @param entities 实体们
     * @param size     插入数量 Db.saveBatch 默认为 1000
     */
    default void insertBatch(Collection<S> entities, int size) {
        Db.saveBatch(entities, size);
    }

    default void updateBatch(S update) {
        update(update, new QueryWrapper<>());
    }

    default void updateBatch(Collection<S> entities) {
        Db.updateBatchById(entities);
    }

    default void updateBatch(Collection<S> entities, int size) {
        Db.updateBatchById(entities, size);
    }

    default void insertOrUpdate(S entity) {
        Db.saveOrUpdate(entity);
    }

    default void insertOrUpdateBatch(Collection<S> collection) {
        Db.saveOrUpdateBatch(collection);
    }

    default int delete(String field, String value) {
        return delete(new QueryWrapper<S>().eq(field, value));
    }

    default int delete(SFunction<S, ?> field, Object value) {
        return delete(new LambdaQueryWrapper<S>().eq(field, value));
    }

}
