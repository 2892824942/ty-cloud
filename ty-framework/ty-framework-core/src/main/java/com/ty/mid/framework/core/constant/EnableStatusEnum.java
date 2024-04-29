package com.ty.mid.framework.core.constant;

import cn.hutool.core.util.ObjUtil;
import com.ty.mid.framework.common.pojo.KVResp;
import io.github.linpeilie.annotations.AutoEnumMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举 <p>
 * @author suyoulinag 
 */
@Getter
@AllArgsConstructor
@AutoEnumMapper("key")
public enum EnableStatusEnum implements KVResp<Integer, String> {

    ENABLE(0, "开启"),
    DISABLE(1, "关闭");


    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;


    public static boolean isEnable(Integer status) {
        return ObjUtil.equal(ENABLE.status, status);
    }

    public static boolean isDisable(Integer status) {
        return ObjUtil.equal(DISABLE.status, status);
    }

    @Override
    public Integer getKey() {
        return status;
    }

    @Override
    public String getValue() {
        return name;
    }
}
