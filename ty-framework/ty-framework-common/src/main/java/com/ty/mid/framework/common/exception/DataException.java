package com.ty.mid.framework.common.exception;

import com.ty.mid.framework.common.constant.BaseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 数据异常类 <p>
 *
 * @author suyouliang <p>
 * @createTime 2023-08-14 15:21
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
public class DataException extends BizException {

    public DataException(@NotNull BaseCode baseCode) {
        super(baseCode);
    }

    public DataException(String code, String message) {
        super(code, message);
    }

    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataException(Throwable cause) {
        super(cause);
    }

    public DataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static DataException of(String code, String message) {
        return new DataException(code, message);
    }

    public static DataException of(String message) {
        return new DataException(message);
    }

    public static DataException of(@NotNull BaseCode baseCode) {
        return new DataException(baseCode);
    }

}