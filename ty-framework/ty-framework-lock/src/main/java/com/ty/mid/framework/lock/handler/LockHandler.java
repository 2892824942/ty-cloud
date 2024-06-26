package com.ty.mid.framework.lock.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.lock.config.spi.LockSpiClassLoader;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.exception.LockException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * lockHandler顶级接口 <p>
 *
 * @author 苏友良 <p>
 * @since 2022/5/11 <p>
 **/
public interface LockHandler {
    default <T extends LockHandler> void customerHandlerValidate(List<T> lockHandlers, Class<T> customerHandlerClass) {
        if (CollectionUtil.isEmpty(lockHandlers)) {
            Assert.notEmpty(lockHandlers, "No customer %s find in your application resource dir: META-INFO/services", customerHandlerClass.getName());
        }
    }

    default <T extends LockHandler> T getCustomerHandler(Class<T> customerHandlerClass) {
        List<T> loaderHandler = LockSpiClassLoader.getInstance().getLoadingClass(customerHandlerClass);
        customerHandlerValidate(loaderHandler, customerHandlerClass);
        return loaderHandler.iterator().next();
    }


    default void handleCustomException(LockInfo lockInfo, String defaultErrorMsg) {
        Class<? extends RuntimeException> exceptionClass = lockInfo.getExceptionClass();
        String errorMsg = StringUtils.isEmpty(lockInfo.getExceptionMsg()) ? defaultErrorMsg : new String(lockInfo.getExceptionMsg().getBytes(), StandardCharsets.UTF_8);
        if (Objects.nonNull(exceptionClass)) {
            Constructor<? extends RuntimeException> constructor;
            try {
                constructor = exceptionClass.getDeclaredConstructor(String.class);
                constructor.setAccessible(true);
                throw constructor.newInstance(errorMsg);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException e) {
                throw new FrameworkException("exception when create customerException:" + exceptionClass.getName(), e);
            }
        }
        throw new LockException(errorMsg);
    }
}
