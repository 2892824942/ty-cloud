package com.ty.mid.framework.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD,})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RC4Encrypt {

    String[] value() default "";
}