package com.ty.mid.framework.idempotent.exception;

/**
 * 服务已经执行过异常
 *
 * @author suyouliang
 * @createTime 2019-08-15 14:26
 */
public class AlreadyExecutedIdempotentException extends IdempotentException {

    public AlreadyExecutedIdempotentException(String code, String message) {
        super(code, message);
    }

    public AlreadyExecutedIdempotentException() {
    }

    public AlreadyExecutedIdempotentException(String message) {
        super(message);
    }

    public AlreadyExecutedIdempotentException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExecutedIdempotentException(Throwable cause) {
        super(cause);
    }

    public AlreadyExecutedIdempotentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}