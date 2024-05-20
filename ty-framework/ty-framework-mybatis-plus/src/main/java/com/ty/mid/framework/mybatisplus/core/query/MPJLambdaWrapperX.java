package com.ty.mid.framework.mybatisplus.core.query;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.config.ConfigProperties;
import com.github.yulichang.toolkit.Constant;
import com.github.yulichang.toolkit.LambdaUtils;
import com.github.yulichang.toolkit.MPJWrappers;
import com.github.yulichang.toolkit.TableList;
import com.github.yulichang.toolkit.support.ColumnCache;
import com.github.yulichang.wrapper.MPJAbstractLambdaWrapper;
import com.github.yulichang.wrapper.interfaces.Chain;
import com.github.yulichang.wrapper.interfaces.Query;
import com.github.yulichang.wrapper.interfaces.QueryLabel;
import com.github.yulichang.wrapper.interfaces.SelectWrapper;
import com.github.yulichang.wrapper.resultmap.Label;
import com.github.yulichang.wrapper.segments.*;
import com.ty.mid.framework.common.constant.DefaultTypeEnum;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.mybatisplus.entity.TimeRangeDO;
import com.ty.mid.framework.mybatisplus.util.WrapperUtils;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 参考 {@link com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper} <p>
 * Lambda 语法使用 Wrapper <p>
 * 由于原MPJLambdaWrapper某些链式调用方法返回为具体的类型,导致直接继承无法丝滑使用链式调用,这里直接继承父AbstractLambdaWrapper <p>
 * 也可以直接继承MPJLambdaWrapper重写父类所有方法(包括MPJAbstractLambdaWrapper更上层的父类方法,这种方式比较麻烦) <p>
 * @author yulichang
 */
@SuppressWarnings({"unused"})
public class MPJLambdaWrapperX<T> extends MPJAbstractLambdaWrapper<T, MPJLambdaWrapperX<T>> implements
        Query<MPJLambdaWrapperX<T>>, QueryLabel<MPJLambdaWrapperX<T>>, Chain<T>, SelectWrapper<T, MPJLambdaWrapperX<T>> {


    /**
     * 查询的字段
     */
    @Getter
    private final List<Select> selectColumns = new ArrayList<>();
    /**
     * 映射关系
     */
    @Getter
    private final List<Label<?>> resultMapMybatisLabel = new ArrayList<>();
    /**
     * 查询字段 sql
     */
    private SharedString sqlSelect = new SharedString();
    /**
     * 是否 select distinct
     */
    private boolean selectDistinct = false;
    /**
     * union sql
     */
    private SharedString unionSql;
    /**
     * 自定义wrapper索引
     */
    private AtomicInteger wrapperIndex;
    /**
     * 自定义wrapper
     */
    @Getter
    private Map<String, Wrapper<?>> wrapperMap;

    /**
     * 推荐使用 带 class 的构造方法
     */
    public MPJLambdaWrapperX() {
        super();
    }

    /**
     * 推荐使用此构造方法
     */
    public MPJLambdaWrapperX(Class<T> clazz) {
        super(clazz);
    }

    /**
     * 构造方法
     *
     * @param entity 主表实体
     */
    public MPJLambdaWrapperX(T entity) {
        super(entity);
    }

    /**
     * 自定义主表别名
     */
    public MPJLambdaWrapperX(String alias) {
        super(alias);
    }

    /**
     * 构造方法
     *
     * @param clazz 主表class类
     * @param alias 主表别名
     */
    public MPJLambdaWrapperX(Class<T> clazz, String alias) {
        super(clazz, alias);
    }

    /**
     * 构造方法
     *
     * @param entity 主表实体类
     * @param alias  主表别名
     */
    public MPJLambdaWrapperX(T entity, String alias) {
        super(entity, alias);
    }

    /****************************************原类的方法******************************************/
    /**
     * 不建议直接 new 该实例，使用 JoinWrappers.lambda(UserDO.class)
     */
    MPJLambdaWrapperX(T entity, Class<T> entityClass, SharedString sqlSelect, AtomicInteger paramNameSeq,
                      Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
                      SharedString lastSql, SharedString sqlComment, SharedString sqlFirst,
                      TableList tableList, Integer index, String keyWord, Class<?> joinClass, String tableName) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
        this.tableList = tableList;
        this.index = index;
        this.keyWord = keyWord;
        this.joinClass = joinClass;
        this.tableName = tableName;
    }

    public <R> MPJLambdaWrapperX<T> likeIfPresent(SFunction<R, ?> column, String val) {
        MPJWrappers.lambdaJoin().like(column, val);
        if (org.springframework.util.StringUtils.hasText(val)) {
            return super.like(column, val);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> inIfPresent(SFunction<R, ?> column, Collection<?> values) {
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            return super.in(column, values);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> inIfPresent(SFunction<R, ?> column, Object... values) {
        if (ObjectUtil.isAllNotEmpty(values) && !ArrayUtil.isEmpty(values)) {
            return super.in(column, values);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> eqIfPresent(SFunction<R, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return super.eq(column, val);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> neIfPresent(SFunction<R, ?> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
            return super.ne(column, val);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> gtIfPresent(SFunction<R, ?> column, Object val) {
        if (val != null) {
            return super.gt(column, val);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> geIfPresent(SFunction<R, ?> column, Object val) {
        if (val != null) {
            return super.ge(column, val);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> ltIfPresent(SFunction<R, ?> column, Object val) {
        if (val != null) {
            return super.lt(column, val);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> leIfPresent(SFunction<R, ?> column, Object val) {
        if (val != null) {
            return super.le(column, val);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> betweenIfPresent(SFunction<R, ?> column, Object val1, Object val2) {
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

    public <R> MPJLambdaWrapperX<T> betweenIfPresent(SFunction<R, ?> column, Object[] values) {
        Object val1 = com.ty.mid.framework.common.util.collection.ArrayUtils.get(values, 0);
        Object val2 = com.ty.mid.framework.common.util.collection.ArrayUtils.get(values, 1);
        return betweenIfPresent(column, val1, val2);
    }

    /**
     * 对字段添加时效条件查询
     *
     * 开始时间字段名称: from_date
     * 结束时间字段名称： to_date
     *
     * 查询条件举例:  from_date >= now() and (to_date <= to_date)
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public MPJLambdaWrapperX<T> addTimelinessQuery(LocalDateTime fromDate, LocalDateTime toDate) {
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
     *
     * 开始时间字段名称: from_date
     * 结束时间字段名称：to_date
     *
     * 查询条件举例:  from_date >= now
     *
     * @return
     */
    public MPJLambdaWrapperX<T> addDefaultTimelinessQuery() {
        return this.addTimelinessQuery(LocalDateTime.now(), null);
    }

    /**
     * sql去重
     * select distinct
     */
    public <R> MPJLambdaWrapperX<T> distinct() {
        this.selectDistinct = true;
        return typedThis;
    }


    @Override
    public List<Select> getSelectColum() {
        return this.selectColumns;
    }

    @Override
    public void addLabel(Label<?> label) {
        this.resultMap = true;
        this.resultMapMybatisLabel.add(label);
    }

    @Override
    public MPJLambdaWrapperX<T> getChildren() {
        return typedThis;
    }


    /**
     * 设置查询字段
     *
     * @param columns 字段数组
     * @return children
     */
    @SafeVarargs
    @Override
    public final <E> MPJLambdaWrapperX<T> select(SFunction<E, ?>... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            Class<?> aClass = LambdaUtils.getEntityClass(columns[0]);
            Map<String, SelectCache> cacheMap = ColumnCache.getMapField(aClass);
            for (SFunction<E, ?> s : columns) {
                SelectCache cache = cacheMap.get(LambdaUtils.getName(s));
                getSelectColum().add(new SelectNormal(cache, index, hasAlias, alias));
            }
        }
        return typedThis;
    }

    @Override
    public MPJLambdaWrapperX<T> selectAll(Class<?> clazz) {
        return Query.super.selectAll(clazz);
    }

    /**
     * 子查询
     */
    public <E, F> MPJLambdaWrapperX<T> selectSub(Class<E> clazz, Consumer<MPJLambdaWrapperX<E>> consumer, SFunction<F, ?> alias) {
        return selectSub(clazz, ConfigProperties.subQueryAlias, consumer, alias);
    }

    /**
     * 子查询
     */
    @SuppressWarnings("DuplicatedCode")
    public <E, F> MPJLambdaWrapperX<T> selectSub(Class<E> clazz, String st, Consumer<MPJLambdaWrapperX<E>> consumer, SFunction<F, ?> alias) {
        MPJLambdaWrapperX<E> wrapper = new MPJLambdaWrapperX<E>(null, clazz, SharedString.emptyString(), paramNameSeq, paramNameValuePairs,
                new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString(),
                new TableList(), null, null, null, null) {
        };
        wrapper.tableList.setAlias(st);
        wrapper.tableList.setRootClass(clazz);
        wrapper.tableList.setParent(this.tableList);
        wrapper.alias = st;
        wrapper.subTableAlias = st;
        consumer.accept(wrapper);
        addCustomWrapper(wrapper);
        String sql = WrapperUtils.buildSubSqlByWrapper(clazz, wrapper, LambdaUtils.getName(alias));
        this.selectColumns.add(new SelectString(sql, hasAlias, this.alias));
        return typedThis;
    }

    /**
     * union
     */
    @SuppressWarnings("UnusedReturnValue")
    public final MPJLambdaWrapperX<T> union(MPJLambdaWrapperX<?>... wrappers) {
        StringBuilder sb = new StringBuilder();
        for (MPJLambdaWrapperX<?> wrapper : wrappers) {
            addCustomWrapper(wrapper);
            Class<?> entityClass = wrapper.getEntityClass();
            Assert.notNull(entityClass, "请使用 new MPJLambdaWrapper(主表.class) 或 JoinWrappers.lambda(主表.class) 构造方法");
            sb.append(" UNION ")
                    .append(WrapperUtils.buildUnionSqlByWrapper(entityClass, wrapper));
        }
        if (Objects.isNull(unionSql)) {
            unionSql = SharedString.emptyString();
        }
        unionSql.setStringValue(unionSql.getStringValue() + sb);
        return typedThis;
    }

    /**
     * union all
     */
    @SafeVarargs
    public final <E, F> MPJLambdaWrapperX<T> unionAll(MPJLambdaWrapperX<T>... wrappers) {
        StringBuilder sb = new StringBuilder();
        for (MPJLambdaWrapperX<?> wrapper : wrappers) {
            addCustomWrapper(wrapper);
            Class<?> entityClass = wrapper.getEntityClass();
            Assert.notNull(entityClass, "请使用 new MPJLambdaWrapper(主表.class) 或 JoinWrappers.lambda(主表.class) 构造方法");
            sb.append(" UNION ALL ")
                    .append(WrapperUtils.buildUnionSqlByWrapper(entityClass, wrapper));
        }
        if (Objects.isNull(unionSql)) {
            unionSql = SharedString.emptyString();
        }
        unionSql.setStringValue(unionSql.getStringValue() + sb);
        return typedThis;
    }

    @SuppressWarnings("DuplicatedCode")
    private void addCustomWrapper(MPJLambdaWrapperX<?> wrapper) {
        if (Objects.isNull(wrapperIndex)) {
            wrapperIndex = new AtomicInteger(0);
        }
        int index = wrapperIndex.incrementAndGet();
        if (Objects.isNull(wrapperMap)) {
            wrapperMap = new HashMap<>();
        }
        String key = "ew" + index;
        wrapper.setParamAlias(wrapper.getParamAlias() + ".wrapperMap." + key);
        wrapperMap.put(key, wrapper);
    }

    /**
     * 查询条件 SQL 片段
     */
    @Override
    @SuppressWarnings("DuplicatedCode")
    public String getSqlSelect() {
        if (StringUtils.isBlank(sqlSelect.getStringValue()) && CollectionUtils.isNotEmpty(selectColumns)) {
            String s = selectColumns.stream().map(i -> {
                if (i.isStr()) {
                    return i.getColumn();
                }
                String prefix;
                if (i.isHasTableAlias()) {
                    prefix = i.getTableAlias();
                } else {
                    if (i.isLabel()) {
                        if (i.isHasTableAlias()) {
                            prefix = i.getTableAlias();
                        } else {
                            prefix = tableList.getPrefix(i.getIndex(), i.getClazz(), true);
                        }
                    } else {
                        prefix = tableList.getPrefix(i.getIndex(), i.getClazz(), false);
                    }
                }
                String str = prefix + StringPool.DOT + i.getColumn();
                if (i.isFunc()) {
                    SelectFunc.Arg[] args = i.getArgs();
                    if (Objects.isNull(args) || args.length == 0) {
                        return String.format(i.getFunc().getSql(), str) + Constant.AS + i.getAlias();
                    } else {
                        return String.format(i.getFunc().getSql(), Arrays.stream(args).map(arg -> {
                            String prefixByClass = tableList.getPrefixByClass(arg.getClazz());
                            Map<String, SelectCache> mapField = ColumnCache.getMapField(arg.getClazz());
                            SelectCache cache = mapField.get(arg.getProp());
                            return prefixByClass + StringPool.DOT + cache.getColumn();
                        }).toArray()) + Constant.AS + i.getAlias();
                    }
                } else {
                    return i.isHasAlias() ? (str + Constant.AS + i.getAlias()) : str;
                }
            }).collect(Collectors.joining(StringPool.COMMA));
            sqlSelect.setStringValue(s);
        }
        return sqlSelect.getStringValue();
    }

    @Override
    public String getUnionSql() {
        return Optional.ofNullable(unionSql).map(SharedString::getStringValue).orElse(StringPool.EMPTY);
    }

    public boolean getSelectDistinct() {
        return selectDistinct;
    }

    /**
     * 用于生成嵌套 sql
     *故 sqlSelect 不向下传递</p>
     */
    @Override
    protected MPJLambdaWrapperX<T> instance() {
        return instance(index, null, null, null);
    }

    @Override
    protected MPJLambdaWrapperX<T> instanceEmpty() {
        return new MPJLambdaWrapperX<>();
    }

    @Override
    protected MPJLambdaWrapperX<T> instance(Integer index, String keyWord, Class<?> joinClass, String tableName) {
        return new MPJLambdaWrapperX<>(getEntity(), getEntityClass(), null, paramNameSeq, paramNameValuePairs,
                new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString(),
                this.tableList, index, keyWord, joinClass, tableName);
    }

    @Override
    public void clear() {
        super.clear();
        selectDistinct = false;
        sqlSelect.toNull();
        selectColumns.clear();
        wrapperIndex = new AtomicInteger(0);
        wrapperMap.clear();
        resultMapMybatisLabel.clear();
    }
}
