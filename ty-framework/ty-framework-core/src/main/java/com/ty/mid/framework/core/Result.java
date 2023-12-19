package com.ty.mid.framework.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ty.mid.framework.common.util.Validator;

import java.io.Serializable;
import java.util.Map;

public interface Result<T> extends Serializable {


    String getMessage();

    T getData();

    @JsonIgnore
    default public T getDataNotNull() {
        Validator.requireNonNull(getData(), "返回值为null");
        return getData();
    }

    @JsonIgnore
    void setExtraData(String key, Object value);

    @JsonIgnore
    Map<String, Object> getExtraData();

    @JsonIgnore
    boolean isSuccess();

}
