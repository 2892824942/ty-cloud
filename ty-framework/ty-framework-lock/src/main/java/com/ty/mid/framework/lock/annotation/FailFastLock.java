package com.ty.mid.framework.lock.annotation;


import java.lang.annotation.*;

/**
 * @author suyouliang
 * @date 2022/03/26
 * Content :加锁注解
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Lock
@Documented
public @interface FailFastLock {
    /**
     * 锁的名称
     *
     * @return name
     */
    String name() default "";

    /**
     * 自定义业务key
     *
     * @return keys
     */
    String[] keys() default {};

}
