package com.ty.mid.framework.web.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MvcInterceptor {

    boolean enable() default true;

}
