package com.ty.mid.framework.core.util;

import cn.hutool.core.lang.Assert;
import org.springframework.core.GenericTypeResolver;

public class SpringTypeUtil {
    public static Class<?> resolveTypeArguments(Class<?> clazz, Class<?> genericIfc, int index) {
        Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(clazz, genericIfc);
        Assert.notEmpty(classes, "Unable to determine the generic type");
        Assert.isTrue(classes.length > index, "Index out of bounds");

        return classes[index];
    }

    public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        return GenericTypeResolver.resolveTypeArguments(clazz, genericIfc);
    }

}
