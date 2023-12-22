package com.ty.mid.framework.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;


@Schema(description="key-value顶层定义")
public interface KVBaseResp<K,V> extends Serializable {
    @JsonIgnore
    K getKey();
    @JsonIgnore
    V getValue();


}
