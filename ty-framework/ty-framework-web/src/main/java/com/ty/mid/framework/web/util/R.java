package com.ty.mid.framework.web.util;

import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.core.util.StringUtils;
import com.ty.mid.framework.web.model.WebResult;

import java.util.Collections;
import java.util.Map;

public abstract class R {

    public static <T> WebResult<T> ofSuccess(T data) {
        WebResult<T> result = new WebResult<>();
        result.setData(data);

        return result;
    }

    public static <T> WebResult<T> ofFail(String errorCode, String message, T data) {
        WebResult<T> result = new WebResult<>();
        result.setCode(errorCode);
        result.setMessage(message);
        result.setData(data);

        return result;
    }

    public static WebResult<Map<?, ?>> ofFail(String errorCode, String message) {
        WebResult<Map<?, ?>> result = new WebResult<>();
        result.setCode(errorCode);
        result.setMessage(message);
        result.setData(Collections.emptyMap());

        return result;
    }

    public static WebResult<Map<?, ?>> ofFail(String message) {
        WebResult<Map<?, ?>> result = new WebResult<>();
        result.setCode("500");
        result.setMessage(message);
        result.setData(Collections.emptyMap());

        return result;
    }

    public static WebResult<Map<?, ?>> ofFail(FrameworkException e) {
        WebResult<Map<?, ?>> result = new WebResult<>();
        result.setCode(StringUtils.isEmpty(e.getCode()) ? "500" : e.getCode());
        result.setMessage(e.getMessage());
        result.setData(Collections.emptyMap());

        return result;
    }

    public static WebResult<Map<?, ?>> ofFail(Exception e) {
        return ofFail(e.getMessage());
    }

}
