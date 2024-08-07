package com.ty.mid.framework.encrypt.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler.DesensitizationHandler;
import com.ty.mid.framework.encrypt.serializer.EncryptionSerializer;

import java.lang.annotation.*;

/**
 * 顶级脱敏注解，自定义注解需要使用此注解 <p>
 *
 * @author suyouliang
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside // 此注解是其他所有 jackson 注解的元注解，打上了此注解的注解表明是 jackson 注解的一部分
@JsonSerialize(using = EncryptionSerializer.class) // 指定序列化器
@EncryptField
public @interface Desensitize {

    /**
     * 脱敏处理器
     */
    @SuppressWarnings("rawtypes")
    Class<? extends DesensitizationHandler> handler();

}
