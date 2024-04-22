package com.ty.mid.framework.common.lang;

import java.lang.annotation.*;

/**
 * 线程安全注解，仅作为标记使用
 *
 * @author suyouliang
 * @createTime 2023-08-15 17:50
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThreadSafe {
}