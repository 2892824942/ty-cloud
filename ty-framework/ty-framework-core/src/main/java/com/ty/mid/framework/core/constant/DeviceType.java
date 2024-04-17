package com.ty.mid.framework.core.constant;

import com.ty.mid.framework.common.pojo.KVResp;
import io.github.linpeilie.annotations.AutoEnumMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型
 * 针对一套 用户体系
 *
 * @author Lion Li
 */
@Getter
@AllArgsConstructor
@AutoEnumMapper("key")
public enum DeviceType implements KVResp<String, String> {

    /**
     * pc端
     */
    PC("1","pc端"),

    /**
     * app端
     */
    APP("2","app端"),

    /**
     * 小程序端
     */
    XCX("3","小程序端");

    private final String code;

    private final String desc;

    @Override
    public String getKey() {
        return code;
    }

    @Override
    public String getValue() {
        return desc;
    }
}
