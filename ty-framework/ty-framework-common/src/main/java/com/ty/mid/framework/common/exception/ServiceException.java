package com.ty.mid.framework.common.exception;

import com.ty.mid.framework.common.constant.BaseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 业务逻辑异常 Exception
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ServiceException extends BizException {


    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    public ServiceException(Integer code, String message) {
        this(String.valueOf(code), message);
    }

    public ServiceException(@NotNull BaseCode baseCode) {
        super(baseCode);
    }

    public ServiceException(String code, String message) {
        super(code, message);
    }

    public ServiceException(String message) {
        super(message);
    }

    public static ServiceException of(@NotNull BaseCode baseCode) {
        return new ServiceException(baseCode);
    }

    public static ServiceException of(String code, String message) {
        return new ServiceException(code, message);
    }

    public static ServiceException of(String message) {
        return new ServiceException(message);
    }


}
