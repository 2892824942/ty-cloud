package com.ty.mid.framework.common.exception;

import com.ty.mid.framework.common.constant.BaseCode;
import com.ty.mid.framework.common.exception.enums.GlobalErrorCodeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 业务异常顶级父类 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-14 15:21 
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class BaseException extends RuntimeException {

    protected String code = GlobalErrorCodeEnum.EXCEPTION.getCode();

    protected String message;
    public BaseException(@NotNull BaseCode baseCode) {
        super(baseCode.getMessage());
        this.code = baseCode.getCode();
        this.message = baseCode.getMessage();
    }

    public BaseException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public BaseException(Throwable cause) {
        super(cause);
        if (cause != null && cause.getMessage() != null) {
            this.message = cause.getMessage();
        }
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
    }

}