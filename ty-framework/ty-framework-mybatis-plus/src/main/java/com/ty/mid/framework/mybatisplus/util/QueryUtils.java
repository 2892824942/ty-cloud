package com.ty.mid.framework.mybatisplus.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ty.mid.framework.common.constant.DomainConstant;
import com.ty.mid.framework.common.entity.AbstractDO;
import com.ty.mid.framework.common.entity.SoftDeletable;
import com.ty.mid.framework.common.util.Validator;

import java.util.List;
import java.util.regex.Pattern;

public abstract class QueryUtils {

    public static final Pattern SORT_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+(\\sasc|\\sdesc)?(,[a-zA-Z0-9]+(\\sasc|\\sdesc)?)?$");

    public static <T extends AbstractDO<?>> QueryWrapper<T> createQuery(Class<T> entityClass) {
        Validator.requireNonNull(entityClass, "entity class can not be null!");

        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Validator.requireNonNull(tableInfo, "can not obtain table info!");

        boolean isSoftDeletable = SoftDeletable.class.isAssignableFrom(entityClass);

        QueryWrapper<T> wrapper = new QueryWrapper<>();

        // 过滤软删除
        if (isSoftDeletable) {
            appendSoftDeleteCondition(wrapper);
        }

        return wrapper;
    }

    public static <T extends AbstractDO<?>> void appendSoftDeleteCondition(QueryWrapper<T> wrapper) {
        if (wrapper == null) {
            return;
        }

        wrapper.eq(DomainConstant.Columns.DELETED, Boolean.FALSE);
    }


    public static <T> Page<T> convertPage(IPage<T> page, List<T> records) {
        Validator.requireNonNull(page, "参数不能为空");
        Page<T> ret = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        ret.setRecords(records);
        return ret;
    }


    public static boolean isIdNull(Long id) {
        return id == null || id <= 0;
    }
}
