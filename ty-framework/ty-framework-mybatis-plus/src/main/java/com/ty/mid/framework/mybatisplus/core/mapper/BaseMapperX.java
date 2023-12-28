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
import com.github.yulichang.interfaces.MPJBaseJoin;
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
public interface BaseMapperX<T extends BaseIdDO<ID>, ID extends Serializable> extends MPJBaseMapper<T> {
    /**
     * 普通分页
     *
     * @param pageParam
     * @param queryWrapper
     * @return
     */
    default PageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageNo())) {
            List<T> list = selectList(queryWrapper);
            return new PageResult<>(list, (long) list.size());
        }

        // MyBatis Plus 查询
        IPage<T> mpPage = MyBatisUtils.buildPage(pageParam);
        selectPage(mpPage, queryWrapper);
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    /**
     * 连表分页
     * 其他连表能力,直接调用父级方法
     *
     * @param pageParam
     * @param queryWrapper
     * @return
     */
    default <DTO> PageResult<DTO> selectJoinPage(Class<DTO> dtoClass, PageParam pageParam, @Param("ew") MPJBaseJoin<T> queryWrapper) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageNo())) {
            List<DTO> list = selectJoinList(dtoClass, queryWrapper);
            return new PageResult<>(list, (long) list.size());
        }

        // MyBatis Plus 查询
        IPage<DTO> mpPage = MyBatisUtils.buildPage(pageParam);
        selectJoinPage(mpPage, dtoClass, queryWrapper);
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    default T selectOne(String field, Object value) {
        return selectOne(new QueryWrapper<T>().eq(field, value));
    }

    default T selectOne(SFunction<T, ?> field, Object value) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field, value));
    }

    default T selectOne(String field1, Object value1, String field2, Object value2) {
        return selectOne(new QueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2,
                        SFunction<T, ?> field3, Object value3) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2)
                .eq(field3, value3));
    }

    default Long selectCount() {
        return selectCount(new QueryWrapper<>());
    }

    default Long selectCount(String field, Object value) {
        return selectCount(new QueryWrapper<T>().eq(field, value));
    }

    default Long selectCount(SFunction<T, ?> field, Object value) {
        return selectCount(new LambdaQueryWrapper<T>().eq(field, value));
    }

    default List<T> selectList() {
        return selectList(new QueryWrapper<>());
    }

    default Map<ID, T> selectMap() {
        return IterUtil.toMap(selectList(), BaseIdDO::getId);
    }


    default <K> Map<K, T> selectMap(SFunction<T, K> keyField) {
        return IterUtil.toMap(selectList(), keyField);
    }


    default List<T> selectList(SFunction<T, ?> field, Object value) {
        return selectList(new LambdaQueryWrapper<T>().eq(field, value));
    }

    default Map<ID, T> selectMap(SFunction<T, ?> field, Object value) {
        return IterUtil.toMap(selectList(field, value), BaseIdDO::getId);
    }

    default <K> Map<K, T> selectMap(SFunction<T, ?> field, Object value, SFunction<T, K> keyField) {
        return IterUtil.toMap(selectList(field, value), keyField);
    }

    default List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return CollUtil.newArrayList();
        }
        return selectList(new LambdaQueryWrapper<T>().in(field, values));
    }

    default Map<ID, T> selectMap(SFunction<T, ?> field, Collection<?> values) {
        return IterUtil.toMap(selectList(field, values), BaseIdDO::getId);
    }

    default <K> Map<K, T> selectMap(SFunction<T, ?> field, Collection<?> values, SFunction<T, K> keyField) {
        return IterUtil.toMap(selectList(field, values), keyField);
    }

    @Deprecated
    default List<T> selectList(SFunction<T, ?> leField, SFunction<T, ?> geField, Object value) {
        return selectList(new LambdaQueryWrapper<T>().le(leField, value).ge(geField, value));
    }

    default Map<ID, T> selectMap(SFunction<T, ?> leField, SFunction<T, ?> geField, Object value) {
        return IterUtil.toMap(selectList(leField, geField, value), BaseIdDO::getId);
    }

    default <K> Map<K, T> selectMap(SFunction<T, ?> leField, SFunction<T, ?> geField, Object value, SFunction<T, K> keyField) {
        return IterUtil.toMap(selectList(leField, geField, value), keyField);
    }

    default List<T> selectList(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return selectList(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    default Map<ID, T> selectMap(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return IterUtil.toMap(selectList(field1, value1, field2, value2), BaseIdDO::getId);
    }

    default <K> Map<K, T> selectMap(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2, SFunction<T, K> keyField) {
        return IterUtil.toMap(selectList(field1, value1, field2, value2), keyField);
    }

    /**
     * 批量插入，适合大量数据插入
     *
     * @param entities 实体们
     */
    default void insertBatch(Collection<T> entities) {
        Db.saveBatch(entities);
    }

    /**
     * 批量插入，适合大量数据插入
     *
     * @param entities 实体们
     * @param size     插入数量 Db.saveBatch 默认为 1000
     */
    default void insertBatch(Collection<T> entities, int size) {
        Db.saveBatch(entities, size);
    }

    default void updateBatch(T update) {
        update(update, new QueryWrapper<>());
    }

    default void updateBatch(Collection<T> entities) {
        Db.updateBatchById(entities);
    }

    default void updateBatch(Collection<T> entities, int size) {
        Db.updateBatchById(entities, size);
    }

    default void insertOrUpdate(T entity) {
        Db.saveOrUpdate(entity);
    }

    default void insertOrUpdateBatch(Collection<T> collection) {
        Db.saveOrUpdateBatch(collection);
    }

    default int delete(String field, String value) {
        return delete(new QueryWrapper<T>().eq(field, value));
    }

    default int delete(SFunction<T, ?> field, Object value) {
        return delete(new LambdaQueryWrapper<T>().eq(field, value));
    }

}
