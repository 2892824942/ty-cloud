package com.ty.mid.framework.common.lang;

import java.lang.annotation.*;

/**
 * 可空注解，仅作为标记使用
 *
 * @author xuchenglong
 * @createTime 2019-08-15 17:50
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Nullable {
}