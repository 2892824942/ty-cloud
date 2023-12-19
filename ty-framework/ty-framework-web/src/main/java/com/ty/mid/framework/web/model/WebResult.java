package com.ty.mid.framework.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ty.mid.framework.core.Result;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@ToString(exclude = "extraData")
@Data
public class WebResult<T> implements Serializable, Result<T> {

    private String code = "0";

    private String message = "";

    private T data;
    @JsonIgnore
    private Map<String, Object> extraData = new HashMap<>();

    @Override
    public boolean isSuccess() {
        return "0".equals(code);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public void setExtraData(String key, Object value) {
        extraData.put(key, value);
    }

    @Override
    public Map<String, Object> getExtraData() {
        return extraData;
    }
}
