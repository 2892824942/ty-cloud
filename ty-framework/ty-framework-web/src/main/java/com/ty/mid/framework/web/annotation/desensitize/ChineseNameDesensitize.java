package com.ty.mid.framework.web.annotation.desensitize;


import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.ty.mid.framework.web.annotation.desensitize.handler.DefaultSliderDesensitizationHandle;

import java.lang.annotation.*;

/**
 * 中文名 <p>
 * @author suyouliang 
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = DefaultSliderDesensitizationHandle.class)
public @interface ChineseNameDesensitize {

    /**
     * 前缀保留长度
     */
    int prefixKeep() default 1;

    /**
     * 后缀保留长度
     */
    int suffixKeep() default 0;

    /**
     * 替换规则，中文名;比如：刘子豪脱敏之后为刘**
     */
    String replacer() default "*";

}
