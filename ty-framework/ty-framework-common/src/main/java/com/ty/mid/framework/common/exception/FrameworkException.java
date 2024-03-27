package com.ty.mid.framework.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 框架异常类
 *
 * @author suyouliang
 * @createTime 2019-08-14 15:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FrameworkException extends BaseException {

    public FrameworkException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public FrameworkException() {
    }

    public FrameworkException(String message) {
        super(message);
        this.message = message;
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public FrameworkException(Throwable cause) {
        super(cause);
        if (cause != null && cause.getMessage() != null) {
            this.message = cause.getMessage();
        }
    }

    public FrameworkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
    }

    public static FrameworkException of(String code, String message) {
        return new FrameworkException(code, message);
    }

    public static FrameworkException of(String message) {
        return new FrameworkException(message);
    }
}