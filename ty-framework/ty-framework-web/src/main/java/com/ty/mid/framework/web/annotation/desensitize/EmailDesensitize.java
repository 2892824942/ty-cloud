package com.ty.mid.framework.web.annotation.desensitize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.ty.mid.framework.web.annotation.desensitize.handler.DefaultRegexDesensitizationHandle;

import java.lang.annotation.*;

/**
 * 邮箱脱敏注解 <p>
 * @author suyouliang
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = DefaultRegexDesensitizationHandle.class)
public @interface EmailDesensitize {

    /**
     * 匹配的正则表达式
     */
    String regex() default "(^.)[^@]*(@.*$)";

    /**
     * 替换规则，邮箱;
     *
     * 比如：example@gmail.com 脱敏之后为 e****@gmail.com
     */
    String replacer() default "$1****$2";
}
