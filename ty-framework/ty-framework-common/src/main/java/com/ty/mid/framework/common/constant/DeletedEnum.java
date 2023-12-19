package com.ty.mid.framework.common.constant;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum DeletedEnum {
    NO_DELETE(Boolean.FALSE, 0),
    DELETED(Boolean.TRUE, 1),
    UNKNOWN(null, -99),
    ;
    private final Boolean booleanValue;

    private final int intValue;

    DeletedEnum(Boolean booleanValue, int intValue) {
        this.booleanValue = booleanValue;
        this.intValue = intValue;
    }

    public static DeletedEnum valueOf(Boolean booleanValue) {
        if (Objects.isNull(booleanValue)) {
            return DeletedEnum.UNKNOWN;
        }
        if (booleanValue.equals(Boolean.TRUE)) {
            return DeletedEnum.DELETED;
        }
        return DeletedEnum.NO_DELETE;
    }

    public static DeletedEnum valueOf(int intValue) {

        if (Objects.equals(intValue, 0)) {
            return DeletedEnum.NO_DELETE;
        }
        if (Objects.equals(intValue, 1)) {
            return DeletedEnum.DELETED;
        }

        return DeletedEnum.UNKNOWN;
    }
}
