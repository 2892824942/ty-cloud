package com.ty.mid.framework.common.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 业务异常类
 *
 * @author xuchenglong
 * @createTime 2019-08-14 15:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BizException extends BaseException {

    public static BizException of(String code, String message){
        return new BizException(code,message);
    }

    public static BizException of(String message){
        return new BizException(message);
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(String message) {
        super(message);
        this.message = message;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public BizException(Throwable cause) {
        super(cause);
        if (cause != null && cause.getMessage() != null) {
            this.message = cause.getMessage();
        }
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
    }

}