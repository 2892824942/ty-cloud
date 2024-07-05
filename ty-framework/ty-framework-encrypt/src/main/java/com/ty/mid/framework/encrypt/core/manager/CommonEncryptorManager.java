package com.ty.mid.framework.encrypt.core.manager;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.encrypt.annotation.EncryptField;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.context.CommonEncryptContext;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 加密管理类
 *
 * @author suyouliang
 * @version 4.6.0
 */
@Slf4j
public class CommonEncryptorManager extends AbstractEncryptorManager<EncryptField> {

    public CommonEncryptorManager(EncryptorConfig defaultProperties) {
        super(defaultProperties);
    }


    @Override
    public String doEncryptField(String value, Field field) {
        //为null或空字符均不处理
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        CommonEncryptContext encryptContext = initEncryptContext(field);
        return this.encrypt(value, encryptContext);
    }

    @Override
    public String doEncrypt(String value, Annotation annotation) {
        //为null或空字符均不处理
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        CommonEncryptContext encryptContext = initEncryptContext(annotation);
        return this.encrypt(value, encryptContext);
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
        //为null或空字符均不处理
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        CommonEncryptContext encryptContext = initEncryptContext(field);
        return this.decrypt(value, encryptContext);
    }

    @Override
    public String doDecrypt(String value, Annotation annotation) {
        //为null或空字符均不处理
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        CommonEncryptContext encryptContext = initEncryptContext(annotation);
        return this.decrypt(value, encryptContext);
    }

    private CommonEncryptContext initEncryptContext(Field field) {

        EncryptField annotation = field.getAnnotation(EncryptField.class);
        return initEncryptContext(annotation);
    }


    private CommonEncryptContext initEncryptContext(Annotation annotation) {
        if (annotation.annotationType().isAnnotationPresent(EncryptField.class)) {
            throw new FrameworkException("调用错误,必须使用EncryptField注解");
        }

        EncryptField encryptFieldAnnotation = (EncryptField) annotation;
        CommonEncryptContext encryptContext = new CommonEncryptContext();
        encryptContext.setAlgorithm(encryptFieldAnnotation.algorithm() == AlgorithmType.DEFAULT ? defaultProperties.getAlgorithm() : encryptFieldAnnotation.algorithm());
        encryptContext.setEncode(encryptFieldAnnotation.encode() == EncodeType.DEFAULT ? defaultProperties.getEncode() : encryptFieldAnnotation.encode());
        encryptContext.setPassword(StrUtil.isBlank(encryptFieldAnnotation.password()) ? defaultProperties.getPassword() : encryptFieldAnnotation.password());
        encryptContext.setPrivateKey(StrUtil.isBlank(encryptFieldAnnotation.privateKey()) ? defaultProperties.getPrivateKey() : encryptFieldAnnotation.privateKey());
        encryptContext.setPublicKey(StrUtil.isBlank(encryptFieldAnnotation.publicKey()) ? defaultProperties.getPublicKey() : encryptFieldAnnotation.publicKey());
        return encryptContext;

    }

}
