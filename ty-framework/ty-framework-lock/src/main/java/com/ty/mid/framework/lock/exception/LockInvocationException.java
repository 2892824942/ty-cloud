package com.ty.mid.framework.lock.exception;

/**
 * @author suyouliang
 * @since 2023/4/16
 **/
public class LockInvocationException extends RuntimeException {

    public LockInvocationException() {
    }

    public LockInvocationException(String message) {
        super(message);
    }

    public LockInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
