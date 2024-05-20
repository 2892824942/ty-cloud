package com.ty.mid.framework.mybatisplus.util;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.yulichang.adapter.AdapterHelper;
import com.github.yulichang.toolkit.Asserts;
import com.github.yulichang.toolkit.LogicInfoUtils;
import com.github.yulichang.toolkit.TableHelper;
import com.github.yulichang.toolkit.sql.SqlScriptUtils;
import com.ty.mid.framework.mybatisplus.core.query.MPJLambdaWrapperX;

import java.util.Objects;
import java.util.Optional;

/**
 * 由于重写AbstractLambdaWrapper抄过来的类,后续如优化子类链式调用,可优化 <p>
 * @since 1.4.5 
 */
@SuppressWarnings("DuplicatedCode")
public class WrapperUtils {

    public static <T> String buildSubSqlByWrapper(Class<T> clazz, MPJLambdaWrapperX<T> wrapper, String alias) {
        TableInfo tableInfo = TableHelper.get(clazz);
        Asserts.hasTable(tableInfo, clazz);
        String first = Optional.ofNullable(wrapper.getSqlFirst()).orElse(StringPool.EMPTY);
        boolean hasWhere = false;
        String entityWhere = getEntitySql(tableInfo, wrapper);
        if (StringUtils.isNotBlank(entityWhere)) {
            hasWhere = true;
        }
        String mainLogic = mainLogic(hasWhere, clazz, wrapper);
        if (StringUtils.isNotBlank(mainLogic)) {
            hasWhere = true;
        }
        String subLogic = subLogic(hasWhere, wrapper);
        if (StringUtils.isNotBlank(subLogic)) {
            hasWhere = true;
        }
        String sqlSegment = (wrapper.getSqlSegment() != null && StringUtils.isNotBlank(wrapper.getSqlSegment())) ?
                ((wrapper.isEmptyOfNormal() ? StringPool.EMPTY : (hasWhere ? " AND " : " WHERE ")) + wrapper.getSqlSegment()) : StringPool.EMPTY;

        String sqlComment = Optional.ofNullable(wrapper.getSqlComment()).orElse(StringPool.EMPTY);
        return String.format(" (%s SELECT %s FROM %s %s %s %s %s %s %s) AS %s ",
                first,
                wrapper.getSqlSelect(),
                tableInfo.getTableName(),
                wrapper.getAlias(),
                wrapper.getFrom(),
                mainLogic,
                subLogic,
                sqlSegment,
                sqlComment,
                alias);
    }

    public static String buildUnionSqlByWrapper(Class<?> clazz, MPJLambdaWrapperX<?> wrapper) {
        TableInfo tableInfo = TableHelper.get(clazz);
        Asserts.hasTable(tableInfo, clazz);
        String first = Optional.ofNullable(wrapper.getSqlFirst()).orElse(StringPool.EMPTY);
        boolean hasWhere = false;
        String entityWhere = getEntitySql(tableInfo, wrapper);
        if (StringUtils.isNotBlank(entityWhere)) {
            hasWhere = true;
        }
        String mainLogic = mainLogic(hasWhere, clazz, wrapper);
        if (StringUtils.isNotBlank(mainLogic)) {
            hasWhere = true;
        }
        String subLogic = subLogic(hasWhere, wrapper);
        if (StringUtils.isNotBlank(subLogic)) {
            hasWhere = true;
        }
        String sqlSegment = (wrapper.getSqlSegment() != null && StringUtils.isNotBlank(wrapper.getSqlSegment())) ?
                ((wrapper.isEmptyOfNormal() ? StringPool.EMPTY : (hasWhere ? " AND " : " WHERE ")) + wrapper.getSqlSegment()) : StringPool.EMPTY;

        String sqlComment = Optional.ofNullable(wrapper.getSqlComment()).orElse(StringPool.EMPTY);
        return String.format(" %s SELECT %s FROM %s %s %s %s %s %s %s ",
                first,
                wrapper.getSqlSelect(),
                tableInfo.getTableName(),
                wrapper.getAlias(),
                wrapper.getFrom(),
                mainLogic,
                subLogic,
                sqlSegment,
                sqlComment);
    }

    private static <T> String formatParam(MPJLambdaWrapperX<T> wrapper, Object param) {
        final String genParamName = Constants.WRAPPER_PARAM + wrapper.getParamNameSeq().incrementAndGet();
        final String paramStr = wrapper.getParamAlias() + ".paramNameValuePairs." + genParamName;
        wrapper.getParamNameValuePairs().put(genParamName, param);
        return SqlScriptUtils.safeParam(paramStr, null);
    }

    private static String getEntitySql(TableInfo tableInfo, MPJLambdaWrapperX<?> wrapper) {
        Object obj = wrapper.getEntity();
        if (Objects.isNull(obj)) {
            return StringPool.EMPTY;
        }
        StringBuilder sb = new StringBuilder(StringPool.EMPTY);
        for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
            if (AdapterHelper.getTableInfoAdapter().mpjHasLogic(tableInfo) && fieldInfo.isLogicDelete()) {
                continue;
            }
            Object val;
            try {
                val = fieldInfo.getField().get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (Objects.isNull(val)) {
                continue;
            }
            sb.append(" AND ").append(wrapper.getTableList().getPrefixByClass(obj.getClass())).append(Constants.DOT)
                    .append(fieldInfo.getColumn()).append(Constants.EQUALS).append(formatParam(wrapper, val));
        }
        //条件不为空 加上 where
        if (sb.length() > 0) {
            sb.delete(0, 4);
            sb.insert(0, " WHERE ");
        }
        return sb.toString();
    }

    private static String mainLogic(boolean hasWhere, Class<?> clazz, MPJLambdaWrapperX<?> wrapper) {
        String info = LogicInfoUtils.getLogicInfo(null, clazz, true, wrapper.getAlias());
        if (StringUtils.isNotBlank(info)) {
            if (hasWhere) {
                return " AND " + info;
            }
            return " WHERE " + info.substring(4);
        }
        return StringPool.EMPTY;
    }

    private static String subLogic(boolean hasWhere, MPJLambdaWrapperX<?> wrapper) {
        String sql = wrapper.getSubLogicSql();
        if (StringUtils.isNotBlank(sql)) {
            if (hasWhere) {
                return sql;
            }
            return " WHERE " + sql.substring(4);
        }
        return StringPool.EMPTY;
    }
}
