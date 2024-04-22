package com.ty.mid.framework.lock.exception;

/**
 * @author 苏友良
 * @since 2023/4/16
 **/
public class LockException extends RuntimeException {

    public LockException() {
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(Throwable cause) {
        super(cause);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }
}
