package com.ty.mid.framework.common.lang;

import java.lang.annotation.*;

/**
 * 永远不会为空,即使为空也会有默认返回值 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-15 17:50
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeverNull {
}