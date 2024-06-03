package com.ty.mid.framework.encrypt.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler.DefaultSliderDesensitizationEncryptor;

import java.lang.annotation.*;

/**
 * 正则脱敏注解 <p>
 *
 * @author suyouliang
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@Desensitize(handler = DefaultSliderDesensitizationEncryptor.class)
@EncryptField
public @interface RegexDesensitize {

    /**
     * 匹配的正则表达式（默认匹配所有）
     */
    String regex() default "^[\\s\\S]*$";

    /**
     * 替换规则，会将匹配到的字符串全部替换成 replacer
     * <p>
     * 例如：regex=123; replacer=******
     * 原始字符串 123456789
     * 脱敏后字符串 ******456789
     */
    String replacer() default "******";
}
