package com.ty.mid.framework.encrypt.annotation;

import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;

import java.lang.annotation.*;

/**
 * 字段加密注解
 *
 * @author suyouliang
 */
@Documented
@Inherited
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EncryptField
public @interface EncryptField {

    /**
     * 加密算法
     */
    AlgorithmType algorithm() default AlgorithmType.DEFAULT;

    /**
     * 秘钥。AES、SM4需要
     */
    String password() default "";

    /**
     * 公钥。RSA、SM2需要
     */
    String publicKey() default "";

    /**
     * 私钥。RSA、SM2需要
     */
    String privateKey() default "";

    /**
     * 编码方式。对加密算法为BASE64的不起作用
     */
    EncodeType encode() default EncodeType.DEFAULT;

}
