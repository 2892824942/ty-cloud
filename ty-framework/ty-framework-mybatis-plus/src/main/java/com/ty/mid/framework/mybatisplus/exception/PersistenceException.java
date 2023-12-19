package com.ty.mid.framework.mybatisplus.exception;

import com.ty.mid.framework.common.exception.FrameworkException;

public class PersistenceException extends FrameworkException {

    public PersistenceException(String code, String message) {
        super(code, message);
    }

    public PersistenceException() {
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }

    public PersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
