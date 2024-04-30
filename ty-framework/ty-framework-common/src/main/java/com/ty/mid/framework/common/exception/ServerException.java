package com.ty.mid.framework.common.exception;

import com.ty.mid.framework.common.constant.BaseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 服务器异常 Exception 
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ServerException extends BizException {

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServerException() {
    }

    public ServerException(Integer code, String message) {
        this(String.valueOf(code), message);
    }

    public ServerException(@NotNull BaseCode baseCode) {
        super(baseCode);
    }

    public ServerException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ServerException(String message) {
        super(message);
    }

    public static ServerException of(@NotNull BaseCode baseCode) {
        return new ServerException(baseCode);
    }

    public static ServerException of(String code, String message) {
        return new ServerException(code, message);
    }

    public static ServerException of(String message) {
        return new ServerException(message);
    }

}
