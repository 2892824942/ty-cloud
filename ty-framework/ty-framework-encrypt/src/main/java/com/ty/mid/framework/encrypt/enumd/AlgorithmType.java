package com.ty.mid.framework.encrypt.enumd;

import com.ty.mid.framework.encrypt.core.encryptor.*;
import com.ty.mid.framework.encrypt.core.encryptor.common.*;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler.DefaultRegexDesensitizeEncryptor;
import com.ty.mid.framework.encrypt.core.encryptor.hashedid.HashedIdEncryptor;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 算法名称
 *
 * @author suyouliang
 * @version 4.6.0
 */
@Getter
@AllArgsConstructor
public enum AlgorithmType {

    /**
     * 默认走yml配置
     */
    DEFAULT(null),

    /**
     * base64
     */
    BASE64(Base64Encryptor.class),

    /**
     * aes
     */
    AES(AesEncryptor.class),

    /**
     * rsa
     */
    RSA(RsaEncryptor.class),

    /**
     * sm2
     */
    SM2(Sm2Encryptor.class),

    /**
     * sm4
     */
    SM4(Sm4Encryptor.class),
    /**
     * HashId
     */
    HASHED_ID(HashedIdEncryptor .class),

    /**
     * 区间脱敏
     */
   REGEX_DESENSITIZE(DefaultRegexDesensitizeEncryptor.class),

    /**
     * 滑动脱敏
     */
    SLIDER_DESENSITIZE(DefaultRegexDesensitizeEncryptor.class),


    ;

    private final Class<? extends AbstractEncryptor> clazz;
}
