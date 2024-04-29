package com.ty.mid.framework.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 业务异常类 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-14 15:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ParamException extends BizException {

    public ParamException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ParamException(String message) {
        super(message);
        this.message = message;
    }

    public ParamException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public ParamException(Throwable cause) {
        super(cause);
        if (cause != null && cause.getMessage() != null) {
            this.message = cause.getMessage();
        }
    }

    public ParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
    }

    public static ParamException of(String code, String message) {
        return new ParamException(code, message);
    }

    public static ParamException of(String message) {
        return new ParamException(message);
    }

}