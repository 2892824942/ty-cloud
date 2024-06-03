package com.ty.mid.framework.encrypt.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler.DefaultSliderDesensitizationEncryptor;

import java.lang.annotation.*;

/**
 * 车牌号 <p>
 *
 * @author suyouliang
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@Desensitize(handler = DefaultSliderDesensitizationEncryptor.class)
@EncryptField
public @interface CarLicenseDesensitize {

    /**
     * 前缀保留长度
     */
    int prefixKeep() default 3;

    /**
     * 后缀保留长度
     */
    int suffixKeep() default 1;

    /**
     * 替换规则，车牌号;比如：粤A66666 脱敏之后为粤A6***6
     */
    String replacer() default "*";

}
