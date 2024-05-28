package com.ty.mid.framework.idempotent.exception;

/**
 * @author suyouliang <p>
 * @createTime 2023-08-15 14:26
 */
public class IdempotentServiceException extends IdempotentException {

    public IdempotentServiceException(String code, String message) {
        super(code, message);
    }

    public IdempotentServiceException() {
    }

    public IdempotentServiceException(String message) {
        super(message);
    }

    public IdempotentServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotentServiceException(Throwable cause) {
        super(cause);
    }

    public IdempotentServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}