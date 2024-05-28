package com.ty.mid.framework.web.annotation.desensitize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.ty.mid.framework.web.annotation.desensitize.handler.DefaultSliderDesensitizationHandle;

import java.lang.annotation.*;

/**
 * 密码 <p>
 *
 * @author suyouliang
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = DefaultSliderDesensitizationHandle.class)
public @interface PasswordDesensitize {

    /**
     * 前缀保留长度
     */
    int prefixKeep() default 0;

    /**
     * 后缀保留长度
     */
    int suffixKeep() default 0;

    /**
     * 替换规则，密码;
     * <p>
     * 比如：123456 脱敏之后为 ******
     */
    String replacer() default "*";

}
