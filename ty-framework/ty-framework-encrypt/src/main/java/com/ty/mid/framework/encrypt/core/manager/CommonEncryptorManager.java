package com.ty.mid.framework.encrypt.core.manager;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.encrypt.annotation.EncryptField;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.context.CommonEncryptContext;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

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
    public String doEncryptField(String value, Field field) {
        //为null或空字符均不处理
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        CommonEncryptContext encryptContext = initEncryptContext(field);
        return this.decrypt(value, encryptContext);
    }

    private CommonEncryptContext initEncryptContext(Field field){

        EncryptField annotation = field.getAnnotation( EncryptField.class);
        CommonEncryptContext encryptContext = new CommonEncryptContext();
        encryptContext.setAlgorithm(annotation.algorithm() == AlgorithmType.DEFAULT ? defaultProperties.getAlgorithm() : annotation.algorithm());
        encryptContext.setEncode(annotation.encode() == EncodeType.DEFAULT ? defaultProperties.getEncode() : annotation.encode());
        encryptContext.setPassword(StrUtil.isBlank(annotation.password()) ? defaultProperties.getPassword() : annotation.password());
        encryptContext.setPrivateKey(StrUtil.isBlank(annotation.privateKey()) ? defaultProperties.getPrivateKey() : annotation.privateKey());
        encryptContext.setPublicKey(StrUtil.isBlank(annotation.publicKey()) ? defaultProperties.getPublicKey() : annotation.publicKey());
        return encryptContext;

    }

}
