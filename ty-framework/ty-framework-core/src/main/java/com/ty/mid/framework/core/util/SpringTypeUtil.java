package com.ty.mid.framework.core.util;

import com.ty.mid.framework.common.util.Validator;
import org.springframework.core.GenericTypeResolver;

public class SpringTypeUtil {
    public static Class<?> resolveTypeArguments(Class<?> clazz, Class<?> genericIfc, int index) {
        Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(clazz, genericIfc);
        Validator.requireNonNull(classes, "Unable to determine the generic type");
        Validator.requireTrue(classes.length > index, "Index out of bounds");

        return classes[index];
    }

    public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        return GenericTypeResolver.resolveTypeArguments(clazz, genericIfc);
    }

}
