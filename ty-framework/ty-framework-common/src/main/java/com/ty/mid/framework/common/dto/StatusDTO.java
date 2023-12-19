package com.ty.mid.framework.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StatusDTO extends CodeTextDTO {
    public StatusDTO() {
    }

    public StatusDTO(String code, String text) {
        super(code, text);
    }
}
