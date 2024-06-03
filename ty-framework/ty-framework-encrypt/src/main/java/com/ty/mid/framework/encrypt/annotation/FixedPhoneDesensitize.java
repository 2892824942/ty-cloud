package com.ty.mid.framework.encrypt.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler.DefaultSliderDesensitizationEncryptor;

import java.lang.annotation.*;

/**
 * 固定电话 <p>
 *
 * @author suyouliang
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@Desensitize(handler = DefaultSliderDesensitizationEncryptor.class)
@EncryptField
public @interface FixedPhoneDesensitize {

    /**
     * 前缀保留长度
     */
    int prefixKeep() default 4;

    /**
     * 后缀保留长度
     */
    int suffixKeep() default 2;

    /**
     * 替换规则，固定电话;比如：01086551122 脱敏之后为 0108*****22
     */
    String replacer() default "*";

}
