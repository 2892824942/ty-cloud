package com.ty.mid.framework.common.exception;

import com.ty.mid.framework.common.constant.BaseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 业务异常顶级父类 <p>
 * 继承此类的接口返回的错误信息将直接通过全局异常处理抛给用户,使用时需区分 <p>
 *
 * @author suyouliang <p>
 * @createTime 2023-08-14 15:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BizException extends BaseException {

    public BizException(@NotNull BaseCode baseCode) {
        super(baseCode);
    }

    public BizException(String code, String message) {
        super(code, message);
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static BizException of(String code, String message) {
        return new BizException(code, message);
    }

    public static BizException of(String message) {
        return new BizException(message);
    }

    public static BizException of(@NotNull BaseCode baseCode) {
        return new BizException(baseCode);
    }

}