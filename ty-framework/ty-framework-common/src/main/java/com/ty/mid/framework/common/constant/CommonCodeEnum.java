package com.ty.mid.framework.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonCodeEnum implements BaseCodeEnum {
    /**
     * 通用返回值 00000,一切ok
     */
    SUCCESS("0", "请求成功"),
    FAIL("1", "请求失败"),
    EXCEPTION("500", "请求异常"),

    ;


    private final String code;
    private final String message;
}
