package com.ty.mid.framework.cache.annotation;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO 自定义注解实现
 */
@Target({ElementType.TYPE, ElementType.METHOD})

@Retention(RetentionPolicy.RUNTIME)
@Cacheable(value = "users")
public @interface BatchCacheable {
    String[] key() default {};
}
