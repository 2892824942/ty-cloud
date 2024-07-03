package com.ty.mid.framework.encrypt.core.encryptor.hashedid;

import com.ty.mid.framework.common.util.HashIdUtil;
import com.ty.mid.framework.encrypt.core.context.HashIdEncryptContext;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;

/**
 * AES算法实现
 *
 * @author suyouliang
 * @version 4.6.0
 */
public class HashedIdEncryptor extends AbstractHashedIdEncryptor {

    public HashedIdEncryptor(HashIdEncryptContext context) {
        super(context);
        context.setAlgorithm(AlgorithmType.HASHED_ID);
    }

    /**
     * 获得当前算法
     */
    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.HASHED_ID;
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    @Override
    public String encrypt(String value, EncodeType encodeType) {
        return HashIdUtil.encode(Long.parseLong(value), context.getSalt(), context.getMinLength());
    }

    /**
     * 解密
     *
     * @param value 待加密字符串
     */
    @Override
    public String decrypt(String value) {
        Long decode = HashIdUtil.decode(value, context.getSalt(), context.getMinLength());
        return decode.toString();
    }
}

