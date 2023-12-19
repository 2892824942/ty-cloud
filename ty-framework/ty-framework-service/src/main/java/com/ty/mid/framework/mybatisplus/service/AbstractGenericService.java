package com.ty.mid.framework.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ty.mid.framework.common.constant.DomainConstant;
import com.ty.mid.framework.common.dto.AbstractDTO;
import com.ty.mid.framework.common.entity.AbstractDO;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.DateUtils;
import com.ty.mid.framework.common.util.UtilGenerics;
import com.ty.mid.framework.common.util.collection.MiscUtils;
import com.ty.mid.framework.core.Converter;
import com.ty.mid.framework.core.util.CollectionUtils;
import com.ty.mid.framework.core.util.StringUtils;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.mybatisplus.util.QueryUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public abstract class AbstractGenericService<T extends AbstractDO<ID>, ID extends Serializable, M extends BaseMapperX<T>, D extends AbstractDTO> extends ServiceImpl<M, T> implements IService<T>, Converter<T, D> {

    /**
     * 创建一个查询包装器
     *
     * @return
     */
    public QueryWrapper<T> createQuery() {
        return QueryUtils.createQuery(currentModelClass());

    }

    /**
     * 对字段添加时效条件查询
     * <p>
     * 开始时间字段名称: from_date
     * 结束时间字段名称： thru_date
     * <p>
     * 查询条件举例:  from_date >= now() and (col_name <= thru_date or thru_date is null )
     *
     * @param query
     * @return
     */
    public QueryWrapper<T> addTimelinessQuery(QueryWrapper<T> query) {
        return this.addTimelinessQuery(query, DomainConstant.Columns.FROM_DATE, DomainConstant.Columns.TO_DATE, DateUtils.now(2));
    }

    /**
     * 对字段添加时效条件查询
     * <p>
     * 开始时间字段名称: from_date
     * 结束时间字段名称：thru_date
     * <p>
     * 查询条件举例:  from_date >= date and (col_name <= thru_date or thru_date is null )
     *
     * @param query
     * @return
     */
    public QueryWrapper<T> addTimelinessQuery(QueryWrapper<T> query, Date date) {
        return this.addTimelinessQuery(query, DomainConstant.Columns.FROM_DATE, DomainConstant.Columns.TO_DATE, date);
    }

    /**
     * 对字段添加时效条件查询
     * <p>
     * 查询条件举例:  from_date >= date and (fromDateColName <= thruDateColName or thru_date is null)
     *
     * @param query
     * @param fromDateColName 开始时间字段名称
     * @param thruDateColName 结束时间字段名称
     * @return
     */
    public QueryWrapper<T> addTimelinessQuery(QueryWrapper<T> query, String fromDateColName, String thruDateColName, Date date) {
        query.le(fromDateColName, date);
        query.and(
                a -> a.ge(thruDateColName, date).or().isNull(thruDateColName)
        );

        return query;
    }

    /**
     * 添加 in 查询条件
     * <p>
     * 如果集合中只有一个元素，则会改为 = 查询
     *
     * @param query
     * @param colName    字段名称
     * @param collection in 集合
     * @return
     */
    public QueryWrapper<T> addCollectionQuery(QueryWrapper<T> query, String colName, Collection<?> collection) {
        if (!CollectionUtils.isEmpty(collection)) {
            if (collection.size() == 1) {
                query.eq(colName, collection.iterator().next());
            } else {
                query.in(colName, collection);
            }
        }

        return query;
    }

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
     * 解析排序表达式
     *
     * @param query
     * @param sort
     */
    public void resolveSort(QueryWrapper<T> query, String sort) {
        if (StringUtils.isEmpty(sort)) {
            return;
        }

        Set<String> orders = StringUtils.commaDelimitedListToSet(sort);

        for (String order : orders) {
            String[] pair = order.split(" ");

            if (pair.length > 2) {
                throw new FrameworkException("排序参数格式错误，支持格式 col_name [asc,desc], sort: " + sort);
            }

            if (pair.length == 1) {
                query.orderByAsc(pair[0]);
                continue;
            }

            boolean desc = pair[1].equalsIgnoreCase("desc");
            if (desc) {
                query.orderByDesc(pair[0]);
            } else {
                query.orderByAsc(pair[0]);
            }
        }

    }

    @Override
    protected Class<T> currentModelClass() {
        return UtilGenerics.cast(ReflectionKit.getSuperClassGenericType(getClass(), IService.class, 0));
    }


    protected Page<T> getOneSizePage() {
        return new Page<>(1, 1);
    }

    protected Page<T> getOneSizePage(OrderItem orderItem) {
        Page<T> page = new Page<>(1, 1);
        if (orderItem != null) {
            page.setOrders(MiscUtils.toList(orderItem));
        }
        return page;
    }

    protected Page<T> getOneSizePage(List<OrderItem> orderItems) {
        Page<T> page = new Page<>(1, 1);
        page.setOrders(orderItems);
        return page;
    }

}
