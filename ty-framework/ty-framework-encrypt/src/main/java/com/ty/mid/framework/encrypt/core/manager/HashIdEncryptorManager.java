package com.ty.mid.framework.encrypt.core.manager;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.common.util.SafeGetUtil;
import com.ty.mid.framework.encrypt.annotation.HashedId;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.context.HashIdEncryptContext;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 加密管理类
 *
 * @author suyouliang
 * @version 4.6.0
 */
@Slf4j
public class HashIdEncryptorManager extends AbstractEncryptorManager<HashedId> {


    public HashIdEncryptorManager(EncryptorConfig defaultProperties) {
        super(defaultProperties);
    }

    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    @Override
    public String doDecryptField(String value, Field field) {
        if (skipHandle(value)) {
            return value;
        }
        HashIdEncryptContext context = doInitEncryptContext();
        return this.decrypt(value, context);
    }

    @Override
    public String doDecrypt(String value, Annotation annotation) {
        if (skipHandle(value)) {
            return value;
        }
        HashIdEncryptContext context = doInitEncryptContext();
        return this.decrypt(value, context);
    }

    @Override
    public String doEncryptField(String value, Field field) {
        if (skipHandle(value)) {
            return value;
        }

        HashIdEncryptContext context = doInitEncryptContext();
        return this.encrypt(value, context);
    }

    @Override
    public String doEncrypt(String value, Annotation annotation) {
        if (skipHandle(value)) {
            return value;
        }
        HashIdEncryptContext context = doInitEncryptContext();
        return this.encrypt(value, context);
    }

    private boolean skipHandle(String value) {
        //为null或空字符均不处理
        if (StrUtil.isEmpty(value)) {
            return Boolean.TRUE;
        }
        EncryptorConfig.HashId hashId = defaultProperties.getHashId();

        if (Objects.isNull(hashId) || !hashId.isEnable()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    private HashIdEncryptContext doInitEncryptContext() {
        EncryptorConfig.HashId hashId = defaultProperties.getHashId();
        HashIdEncryptContext context = new HashIdEncryptContext();
        context.setSalt(SafeGetUtil.getOrDefault(hashId.getSalt(), context.getSalt()));
        context.setMinLength(SafeGetUtil.getOrDefault(hashId.getMinLength(), context::getMinLength));
        context.setAlgorithm(AlgorithmType.HASHED_ID);
        return context;

    }
}
