package com.ty.mid.framework.common.exception;

import com.ty.mid.framework.common.constant.BaseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 框架异常类 <p>
 *
 * @author suyouliang <p>
 * @createTime 2023-08-14 15:21
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class FrameworkException extends BaseException {
    public FrameworkException(@NotNull BaseCode baseCode) {
        super(baseCode);
    }

    public FrameworkException(String code, String message) {
        super(code, message);
    }

    public FrameworkException() {
    }

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameworkException(Throwable cause) {
        super(cause);
    }

    public FrameworkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static FrameworkException of(String code, String message) {
        return new FrameworkException(code, message);
    }

    public static FrameworkException of(String message) {
        return new FrameworkException(message);
    }

    public static FrameworkException of(@NotNull BaseCode baseCode) {
        return new FrameworkException(baseCode);
    }
}