package com.ty.mid.framework.common.constant;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum BooleanEnum {
    TRUE(Boolean.TRUE),
    FALSE(Boolean.FALSE),
    NULL(Boolean.FALSE),
    ;
    private Boolean value;

    BooleanEnum(Boolean value) {
        this.value = value;
    }

    public static BooleanEnum booleanOf(Boolean booleanValue) {
        if (Objects.isNull(booleanValue)) {
            return BooleanEnum.NULL;
        }
        if (booleanValue.equals(Boolean.TRUE)) {
            return BooleanEnum.TRUE;
        }
        return BooleanEnum.FALSE;
    }
}
