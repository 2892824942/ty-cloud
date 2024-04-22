package com.ty.mid.framework.common.lang;

import java.lang.annotation.*;

/**
 * 为空安全,可以放心传入null及空集合
 *
 * @author suyouliang
 * @createTime 2023-08-15 17:50
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NullSafe {
}