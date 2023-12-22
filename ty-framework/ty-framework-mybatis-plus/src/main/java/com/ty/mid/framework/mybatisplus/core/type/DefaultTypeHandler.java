//package com.ty.mid.framework.mybatisplus.core.type;
//
//import cn.hutool.core.lang.Assert;
//import cn.hutool.crypto.SecureUtil;
//import cn.hutool.crypto.symmetric.AES;
//import com.ty.mid.framework.common.util.string.StrUtils;
//import com.ty.mid.framework.core.spring.SpringContextHelper;
//import org.apache.ibatis.type.BaseTypeHandler;
//import org.apache.ibatis.type.JdbcType;
//
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
///**
// * 数据库一般不建议使用null值, 因此一般数据库都会使用notNull的配置,在此前提下
// * 1.未设置数据库默认值,这个时候数据一旦有一个字段为null,会报no default value,此时需要我们手动在代码中判断哪些数据是null,并做初始化
// *
// * 此问题可以通过interceptor处理,但是扫描过程针对所有数据,成本较高
// *
// * 2.设置默认值,数据入库阶段ok了,但会引出另一个问题:数字类的int,bigInt等,给定了初始值,但是存在初始值和实际值不相等的情况,这个时候需要我们手动处理
// *
// * 此类为处理以上2情况,在产品需要暴露给前端,又不想将数据库的默认值显示出来,希望还是显示空
// *
// * @author suyoulinag
// */
//public class DefaultTypeHandler extends BaseTypeHandler<String> {
//
//    @Override
//    public String getResult(ResultSet rs, String columnName) throws SQLException {
//        String value = rs.getString(columnName);
//        return getResult(value);
//    }
//
//    @Override
//    public List<Integer> getResult(ResultSet rs, int columnIndex) throws SQLException {
//        String value = rs.getString(columnIndex);
//        return getResult(value);
//    }
//
//    @Override
//    public List<Integer> getResult(CallableStatement cs, int columnIndex) throws SQLException {
//        String value = cs.getString(columnIndex);
//        return getResult(value);
//    }
//
//    private List<Integer> getResult(String value) {
//        if (value == null) {
//            return null;
//        }
//        return StrUtils.splitToInteger(value, COMMA);
//    }
//
//}
