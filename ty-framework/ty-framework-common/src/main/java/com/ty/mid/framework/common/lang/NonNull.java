package com.ty.mid.framework.common.lang;

import java.lang.annotation.*;

/**
 * 非空注解，仅作为标记使用 <p>
 *
 * @author suyouliang <p>
 * @createTime 2023-08-15 17:50
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NonNull {
}