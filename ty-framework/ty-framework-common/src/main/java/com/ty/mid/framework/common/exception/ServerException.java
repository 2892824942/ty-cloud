package com.ty.mid.framework.common.exception;

import com.ty.mid.framework.common.constant.BaseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    public ServerException(BaseCode baseCode) {
        super(baseCode.getMessage());
        super.code = baseCode.getCode();
    }

    public ServerException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ServerException(String message) {
        super(message);
    }

    public static ServerException of(BaseCode baseCode) {
        return new ServerException(baseCode);
    }

    public static ServerException of(String code, String message) {
        return new ServerException(code, message);
    }

    public static ServerException of(String message) {
        return new ServerException(message);
    }

}
