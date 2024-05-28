package com.ty.mid.framework.idempotent.exception;

import com.ty.mid.framework.common.exception.FrameworkException;

/**
 * 幂等异常 <p>
 *
 * @author suyouliang <p>
 * @createTime 2023-08-15 14:26
 */
public class IdempotentException extends FrameworkException {

    public IdempotentException(String code, String message) {
        super(code, message);
    }

    public IdempotentException() {
    }

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotentException(Throwable cause) {
        super(cause);
    }

    public IdempotentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}