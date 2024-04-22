package com.ty.mid.framework.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 业务异常定级父类
 * 继承此类的接口返回的错误信息将直接通过全局异常处理抛给用户,使用时需区分
 *
 * @author suyouliang
 * @createTime 2023-08-14 15:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BizException extends BaseException {

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

    public static BizException of(String code, String message) {
        return new BizException(code, message);
    }

    public static BizException of(String message) {
        return new BizException(message);
    }

}