package com.ty.mid.framework.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 1.所有需要使用@InEnum注解校验的枚举需要实现
 * 2.所有需要序列化的枚举需要实现
 *
 * @param <K>
 * @param <V>
 */

@Schema(description = "key-value顶层定义")
public interface KVResp<K, V> extends Serializable {
    K getKey();

    V getValue();


}
