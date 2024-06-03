package com.ty.mid.framework.encrypt.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ty.mid.framework.encrypt.serializer.HashedIdJsonDeserializer;
import com.ty.mid.framework.encrypt.serializer.HashedIdJsonSerializer;

import java.lang.annotation.*;

/**
 * hash算法处理的id,隐藏业务序号
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside // 此注解是其他所有 jackson 注解的元注解，打上了此注解的注解表明是 jackson 注解的一部分
@JsonSerialize(using = HashedIdJsonSerializer.class)
@JsonDeserialize(using = HashedIdJsonDeserializer.class)
@EncryptField
public @interface HashedId {
}
