package com.ty.mid.framework.mybatisplus.core.type;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 字段字段的 TypeHandler 实现类，基于 {@link AES} 实现 <p>
 * 可通过 mybatis-plus.encryptor.password 配置项，设置密钥 <p>
 * 关于update过程,typeHandler不生效的问题:<p>
 * 首先该问题存在于低版本中,对于updateById,lambdaUpdateWrapper有不同程度的不兼容,但是当前框架版本是兼容的<p>
 * 另外:对于LambadaUpdateWrapper,不使用entity赋值,而是直接使用set方式,例如<p>
 * update(new LambdaUpdateWrapper<User>().eq(User::getId, userId).set(User::getPassword,password));
 * 这种方式是不生效的,当然,使用Mybatis的Interceptor可以解决以上这个特殊场景,但是,需要自定义加密注解
 * 我的考虑:
 * 1.Mybatis层面使用TypeHandler能做到这个层面完全可以接受,因为这个特殊场景(我个人觉得不支持可以接受,毕竟没有用entity).如果使用Interceptor解决这个问题
 * 对于只读的如DefaultTYpeHandler,显然使用Interceptor有点大材小用,可是如果关于字段处理,有两个处理方案,我觉得是很糟心的
 * 2.对于加密以及逗号拼接转list(或者其他集合),这种场景web上可能也要用到,使用自定义的加密注解,一定程度上,在加密这个领域算是统一了
 *
 * @author suyoulinag
 */
public class EncryptTypeHandler extends BaseTypeHandler<String> {

    private static final String ENCRYPTOR_PROPERTY_NAME = "mybatis-plus.encryptor.password";

    private static AES aes;

    private static String decrypt(String value) {
        //兼容数据库默认值""字符
        if (StrUtil.isEmpty(value)) {
            return value;
        }
        return getEncryptor().decryptStr(value);
    }

    public static String encrypt(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        return getEncryptor().encryptBase64(rawValue);
    }

    private static AES getEncryptor() {
        if (aes != null) {
            return aes;
        }
        // 构建 AES
        //TODO syl 更改为配置类
        String password = SpringContextHelper.getProperty(ENCRYPTOR_PROPERTY_NAME);
        Assert.notEmpty(password, "配置项({}) 不能为空", ENCRYPTOR_PROPERTY_NAME);
        aes = SecureUtil.aes(password.getBytes());
        return aes;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, encrypt(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return decrypt(value);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return decrypt(value);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return decrypt(value);
    }

}
