package com.ty.mid.framework.common.dto;

import lombok.Data;

@Data
public class RangeDTO<T> extends BaseDTO {

    private T low;

    private T high;

    public RangeDTO() {
    }

    public RangeDTO(T low, T high) {
        this.low = low;
        this.high = high;
    }


}
