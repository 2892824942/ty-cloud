package com.ty.mid.framework.encrypt.core.encryptor.common;

import com.ty.mid.framework.encrypt.core.context.CommonEncryptContext;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;
import com.ty.mid.framework.encrypt.utils.EncryptUtils;

/**
 * AES算法实现
 *
 * @author suyouliang
 * @version 4.6.0
 */
public class AesEncryptor extends AbstractCommonEncryptor {

    public AesEncryptor(CommonEncryptContext context) {
        super(context);
    }

    /**
     * 获得当前算法
     */
    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.AES;
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    @Override
    public String encrypt(String value, EncodeType encodeType) {
        if (encodeType == EncodeType.HEX) {
            return EncryptUtils.encryptByAesHex(value, context.getPassword());
        } else {
            return EncryptUtils.encryptByAes(value, context.getPassword());
        }
    }

    /**
     * 解密
     *
     * @param value 待加密字符串
     */
    @Override
    public String decrypt(String value) {
        return EncryptUtils.decryptByAes(value, context.getPassword());
    }
}
