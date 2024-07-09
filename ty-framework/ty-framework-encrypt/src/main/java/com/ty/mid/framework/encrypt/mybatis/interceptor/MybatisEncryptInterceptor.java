package com.ty.mid.framework.encrypt.mybatis.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.ty.mid.framework.encrypt.core.manager.EncryptorManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.PreparedStatement;
import java.util.Properties;

/**
 * 入参加密拦截器
 * 当加载mybatis-plus模块starter时,会加载此配置,具体见
 *
 * @author suyouliang
 * @version 4.6.0
 * @see com.ty.mid.framework.autoconfigure.MybatisEncryptionConfiguration
 */
@Slf4j
@Intercepts({@Signature(
        type = ParameterHandler.class,
        method = "setParameters",
        args = {PreparedStatement.class})
})
@AllArgsConstructor
public class MybatisEncryptInterceptor implements Interceptor {

    private final EncryptorManager encryptorManager;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof ParameterHandler) {
            // 进行加密操作
            ParameterHandler parameterHandler = (ParameterHandler) target;
            Object parameterObject = parameterHandler.getParameterObject();
            if (ObjectUtil.isNotNull(parameterObject) && !(parameterObject instanceof String)) {
                encryptorManager.encryptHandler(parameterObject);
            }
        }
        return target;
    }


    @Override
    public void setProperties(Properties properties) {
    }
}
