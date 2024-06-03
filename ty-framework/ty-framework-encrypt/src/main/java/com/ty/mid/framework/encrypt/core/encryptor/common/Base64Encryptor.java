package com.ty.mid.framework.encrypt.core.encryptor.common;

import com.ty.mid.framework.encrypt.core.encryptor.AbstractNoContextEncryptor;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;
import com.ty.mid.framework.encrypt.utils.EncryptUtils;
import lombok.NoArgsConstructor;

/**
 * Base64算法实现
 *
 * @author suyouliang
 * @version 4.6.0
 */
@NoArgsConstructor
public class Base64Encryptor extends AbstractNoContextEncryptor {

    /**
     * 获得当前算法
     */
    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.BASE64;
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    @Override
    public String encrypt(String value, EncodeType encodeType) {
        return EncryptUtils.encryptByBase64(value);
    }

    /**
     * 解密
     *
     * @param value 待加密字符串
     */
    @Override
    public String decrypt(String value) {
        return EncryptUtils.decryptByBase64(value);
    }
}
