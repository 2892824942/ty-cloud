package com.ty.mid.framework.mybatisplus.core.type;

import com.ty.mid.framework.common.constant.DefaultTypeEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 数据库一般不建议使用null值, 因此一般数据库都会使用notNull的配置,在此前提下 <p>
 * 1.未设置数据库默认值,这个时候数据一旦有一个字段为null,会报no default value,此时需要我们手动在代码中判断哪些数据是null,并做初始化 <p>
 * 此问题可以通过interceptor处理,但是扫描过程针对所有数据,成本较高 <p>
 * 2.数据库设置默认值,数据入库阶段ok了,但会引出另一个问题:int,bigInt等数字类型,给定了初始值会导致前端显示和预期不符(varchar默认空字符和null,前端显示是一致的) <p>
 * 此类为处理以上情况,在产品需要暴露给前端,又不想将数据库的默认值显示出来,希望还是显示空 <p>
 * 目前采用的是2方式使用数据库设置默认值. <p>
 *
 * @author suyoulinag
 */

public class DefaultTypeHandler<T> extends BaseTypeHandler<T> {
    private Object getTypeDefaultResult(Object parameter) {
        Class<?> parameterClass = parameter.getClass();
        DefaultTypeEnum defaultTypeEnum = DefaultTypeEnum.DEFAULT_TYPE_ENUMMAP.get(parameterClass);
        if (Objects.isNull(defaultTypeEnum)) {
            return parameter;
        }
        // parameter是定义的类型实例
        return null;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object result = rs.getObject(columnName);
        return (T) getTypeDefaultResult(result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object result = rs.getObject(columnIndex);
        return (T) getTypeDefaultResult(result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object result = cs.getObject(columnIndex);
        return (T) getTypeDefaultResult(result);
    }
}
