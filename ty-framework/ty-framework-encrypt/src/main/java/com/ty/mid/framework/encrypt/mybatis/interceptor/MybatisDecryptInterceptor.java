package com.ty.mid.framework.encrypt.mybatis.interceptor;

import com.ty.mid.framework.encrypt.core.manager.EncryptorManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Statement;
import java.util.Properties;

/**
 * 出参解密拦截器
 * 当加载mybatis-plus模块starter时,会加载此配置,具体见
 * @see com.ty.mid.framework.autoconfigure.MybatisEncryptionConfiguration
 *
 * @author suyouliang
 * @version 4.6.0
 */
@Slf4j
@Intercepts({@Signature(
        type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {Statement.class})
})
@AllArgsConstructor
public class MybatisDecryptInterceptor implements Interceptor {

    private final EncryptorManager encryptorManager;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取执行mysql执行结果
        Object result = invocation.proceed();
        if (result == null) {
            return null;
        }
        encryptorManager.decryptHandler(result);
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
