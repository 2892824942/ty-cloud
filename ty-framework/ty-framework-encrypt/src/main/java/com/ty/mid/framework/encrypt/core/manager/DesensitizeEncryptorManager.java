package com.ty.mid.framework.encrypt.core.manager;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.encrypt.annotation.Desensitize;
import com.ty.mid.framework.encrypt.annotation.EncryptField;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.context.DesensitizeEncryptContext;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler.DesensitizationHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

/**
 * 加密管理类
 *
 * @author suyouliang
 * @version 4.6.0
 */
@Slf4j
public class DesensitizeEncryptorManager extends AbstractEncryptorManager<Desensitize> {

    public DesensitizeEncryptorManager(EncryptorConfig defaultProperties) {
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
    public String doEncryptField(String value, Field field) {
        DesensitizeEncryptContext context = initEncryptContext(field);
        return this.encrypt(value, context);
    }

    @Override
    public String doEncrypt(String value, Annotation annotation) {
        DesensitizeEncryptContext context = initEncryptContext(annotation);
        return this.encrypt(value, context);
    }

    /**
     * 字段值进行解密。通过字段的批注注册新的加密算法
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
        DesensitizeEncryptContext context = initEncryptContext(field);
        return this.decrypt(value, context);
    }

    @Override
    public String doDecrypt(String value, Annotation annotation) {
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        DesensitizeEncryptContext context = initEncryptContext(annotation);
        return this.decrypt(value, context);
    }


    private DesensitizeEncryptContext initEncryptContext(Field field) {
        Annotation targetAnnotation = Arrays.stream(field.getAnnotations())
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(Desensitize.class) ||
                        annotation.annotationType().isAssignableFrom(EncryptField.class))
                .findFirst()
                .orElse(null);

        return DesensitizationHandler.DesensitizeEnum.toDesensitizeInfo(targetAnnotation);

    }

    private DesensitizeEncryptContext initEncryptContext(Annotation annotation) {
        if (Objects.isNull(annotation)) {
            throw new FrameworkException("不存在有效的加密注解");
        }
        if (!annotation.annotationType().isAnnotationPresent(Desensitize.class) &&
                !annotation.annotationType().isAssignableFrom(EncryptField.class)) {
            throw new FrameworkException("未找到有效的加密注解");
        }
        return DesensitizationHandler.DesensitizeEnum.toDesensitizeInfo(annotation);

    }

}
