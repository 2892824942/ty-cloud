package com.ty.mid.framework.api.switcher.annotation;

import java.lang.annotation.*;

/**
 * Api 开关封装
 *
 * @author suyouliang
 * @createTime 2023-08-14 15:17
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiSwitcher {

    /**
     * api 名称
     *
     * @return
     */
    String name();

    /**
     * 默认提示
     *
     * @return
     */
    String tip() default "";

}
