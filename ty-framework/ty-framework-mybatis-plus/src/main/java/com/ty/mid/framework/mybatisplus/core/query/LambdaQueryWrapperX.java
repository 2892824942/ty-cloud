package com.ty.mid.framework.mybatisplus.core.query;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ty.mid.framework.common.constant.DefaultTypeEnum;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.common.util.collection.ArrayUtils;
import com.ty.mid.framework.mybatisplus.entity.TimeRangeDO;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * 拓展 MyBatis Plus QueryWrapper 类，主要增加如下功能： <p>
 * 1. 拼接条件的方法，增加 xxxIfPresent 方法，用于判断值不存在的时候，不要拼接到条件中。 <p>
 * 由于原LambdaQueryWrapper某些链式调用方法返回为具体的类型,导致直接集成无法丝滑使用链式调用,这里直接继承父AbstractLambdaWrapper <p>
 * 也可以直接继承LambdaQueryWrapper重写父类所有方法(包括MPJAbstractLambdaWrapper更上层的父类方法,这种方式比较麻烦)
 */
public class LambdaQueryWrapperX<T> extends AbstractLambdaWrapper<T, LambdaQueryWrapperX<T>>
        implements Query<LambdaQueryWrapperX<T>, T, SFunction<T, ?>> {


    /**
     * 查询字段
     */
    private SharedString sqlSelect = new SharedString();

    public LambdaQueryWrapperX() {
        this((T) null);
    }

    public LambdaQueryWrapperX(T entity) {
        super.setEntity(entity);
        super.initNeed();
    }

    public LambdaQueryWrapperX(Class<T> entityClass) {
        super.setEntityClass(entityClass);
        super.initNeed();
    }

    LambdaQueryWrapperX(T entity, Class<T> entityClass, SharedString sqlSelect, AtomicInteger paramNameSeq,
                        Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
                        SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.paramAlias = paramAlias;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }

    public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
        if (StringUtils.hasText(val)) {
            return super.like(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> likeLeftIfPresent(SFunction<T, ?> column, String val) {
        if (StringUtils.hasText(val)) {
            return super.likeLeft(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> likeRightIfPresent(SFunction<T, ?> column, String val) {
        if (StringUtils.hasText(val)) {
            return super.likeRight(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            return super.in(column, values);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Object... values) {
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            return super.in(column, values);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return super.eq(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> neIfPresent(SFunction<T, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return super.ne(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return super.gt(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return super.ge(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return super.lt(column, val);
        }
        return this;
    }


    /****************************************原类的方法******************************************/

    public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return super.le(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return super.between(column, val1, val2);
        }
        if (val1 != null) {
            return ge(column, val1);
        }
        if (val2 != null) {
            return le(column, val2);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object[] values) {
        Object val1 = ArrayUtils.get(values, 0);
        Object val2 = ArrayUtils.get(values, 1);
        return betweenIfPresent(column, val1, val2);
    }

    /**
     * 对字段添加时效条件查询
     * <p>
     * 开始时间字段名称: from_date
     * 结束时间字段名称： to_date
     * <p>
     * 查询条件举例:  from_date >= now() and (to_date <= to_date)
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public LambdaQueryWrapperX<T> addTimelinessQuery(LocalDateTime fromDate, LocalDateTime toDate) {
        Class<?> genericType = GenericsUtil.getGenericTypeByIndex(this.getClass(), 0);
        if (TimeRangeDO.class.isAssignableFrom(genericType)) {
            SFunction<TimeRangeDO, ?> fromFunction = TimeRangeDO::getFromDate;
            SFunction<TimeRangeDO, ?> toFunction = TimeRangeDO::getFromDate;
            SFunction<T, ?> tFromFunction = (SFunction<T, ?>) fromFunction;
            SFunction<T, ?> tToFunction = (SFunction<T, ?>) toFunction;
            this.geIfPresent(tFromFunction, fromDate);
            super.and(Objects.nonNull(toDate), q -> q.leIfPresent(tToFunction, toDate).or().eq(tToFunction, DefaultTypeEnum.LOCAL_DATE_TIME.defaultValue()));
            return this;
        }
        throw new FrameworkException("必须继承自TimeRangeDO的实体才可调用此方法");
    }

    /**
     * 对字段添加时效条件查询
     * <p>
     * 开始时间字段名称: from_date
     * 结束时间字段名称：to_date
     * <p>
     * 查询条件举例:  from_date >= now
     *
     * @return
     */
    public LambdaQueryWrapperX<T> addDefaultTimelinessQuery() {
        return this.addTimelinessQuery(LocalDateTime.now(), null);
    }

    @Override
    public LambdaQueryWrapperX<T> select(boolean condition, List<SFunction<T, ?>> columns) {
        return doSelect(condition, columns);
    }

    /**
     * 过滤查询的字段信息(主键除外!)
     * 例1: 只要 java 字段名以 "test" 开头的             -> select(i -&gt; i.getProperty().startsWith("test"))</p>
     * 例2: 只要 java 字段属性是 CharSequence 类型的     -> select(TableFieldInfo::isCharSequence)</p>
     * 例3: 只要 java 字段没有填充策略的                 -> select(i -&gt; i.getFieldFill() == FieldFill.DEFAULT)</p>
     * 例4: 要全部字段                                   -> select(i -&gt; true)</p>
     * 例5: 只要主键字段                                 -> select(i -&gt; false)</p>
     *
     * @param predicate 过滤方式
     * @return this
     */
    @Override
    public LambdaQueryWrapperX<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        if (entityClass == null) {
            entityClass = getEntityClass();
        } else {
            setEntityClass(entityClass);
        }
        Assert.notNull(entityClass, "entityClass can not be null");
        this.sqlSelect.setStringValue(TableInfoHelper.getTableInfo(entityClass).chooseSelect(predicate));
        return typedThis;
    }

    @Override
    @SafeVarargs
    public final LambdaQueryWrapperX<T> select(SFunction<T, ?>... columns) {
        return doSelect(true, CollectionUtils.toList(columns));
    }

    @Override
    @SafeVarargs
    public final LambdaQueryWrapperX<T> select(boolean condition, SFunction<T, ?>... columns) {
        return doSelect(condition, CollectionUtils.toList(columns));
    }

    /**
     * @since 3.5.4
     */
    protected LambdaQueryWrapperX<T> doSelect(boolean condition, List<SFunction<T, ?>> columns) {
        if (condition && CollectionUtils.isNotEmpty(columns)) {
            this.sqlSelect.setStringValue(columnsToString(false, columns));
        }
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return sqlSelect.getStringValue();
    }

    /**
     * 用于生成嵌套 sql
     * 故 sqlSelect 不向下传递</p>
     */
    @Override
    protected LambdaQueryWrapperX<T> instance() {
        return new LambdaQueryWrapperX<>(getEntity(), getEntityClass(), null, paramNameSeq, paramNameValuePairs,
                new MergeSegments(), paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    @Override
    public void clear() {
        super.clear();
        sqlSelect.toNull();
    }


}
