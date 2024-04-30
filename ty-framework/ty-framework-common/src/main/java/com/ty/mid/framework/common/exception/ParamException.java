package com.ty.mid.framework.common.exception;

import com.ty.mid.framework.common.constant.BaseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 业务异常类 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-14 15:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ParamException extends BizException {
    public ParamException(@NotNull BaseCode baseCode) {
        super(baseCode);
    }

    public ParamException(String code, String message) {
        super(code,message);
    }

    public ParamException(String message) {
        super(message);
    }

    public ParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamException(Throwable cause) {
        super(cause);
    }

    public ParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static ParamException of(String code, String message) {
        return new ParamException(code, message);
    }

    public static ParamException of(String message) {
        return new ParamException(message);
    }

    public static ParamException of(@NotNull BaseCode baseCode) {
        return new ParamException(baseCode);
    }

}