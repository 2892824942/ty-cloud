package com.ty.mid.framework.lock.exception;

/**
 * @author
 * @since 2023/4/16
 **/
public class LockTransactionForbiddenException extends RuntimeException {

    public LockTransactionForbiddenException() {
    }

    public LockTransactionForbiddenException(String message) {
        super(message);
    }

    public LockTransactionForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
