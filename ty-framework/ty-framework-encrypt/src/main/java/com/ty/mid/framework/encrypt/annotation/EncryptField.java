package com.ty.mid.framework.encrypt.annotation;

import com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler.DesensitizationHandler;
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

    /**
     * 当algorithm指定为AlgorithmType.REGEX_DESENSITIZE或AlgorithmType.SLIDER_DESENSITIZE时必须指定这个字段.
     * 不推荐使用这种方式.但考虑有人喜欢使用统一的注解,方便查询保留了
     * 推荐直接使用脱敏注解,如@PasswordDesensitize,@MobileDesensitize等
     */
    DesensitizationHandler.DesensitizeEnum desensitizeType() default DesensitizationHandler.DesensitizeEnum.DEFAULT;

}
