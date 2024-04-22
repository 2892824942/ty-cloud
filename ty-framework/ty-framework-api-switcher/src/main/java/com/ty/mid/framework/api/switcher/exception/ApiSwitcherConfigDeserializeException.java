package com.ty.mid.framework.api.switcher.exception;

import com.ty.mid.framework.common.exception.FrameworkException;

/**
 * api 开关配置反序列化异常
 *
 * @author suyouliang
 * @createTime 2023-08-14 16:50
 */
public class ApiSwitcherConfigDeserializeException extends FrameworkException {

    public ApiSwitcherConfigDeserializeException(String message) {
        super(message);
    }
}
