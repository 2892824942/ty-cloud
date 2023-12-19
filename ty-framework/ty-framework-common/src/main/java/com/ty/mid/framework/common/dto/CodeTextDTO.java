package com.ty.mid.framework.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 封装基础字典、状态信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CodeTextDTO extends AbstractDTO {

    private String code;

    private String text;

    public CodeTextDTO() {
    }

    public CodeTextDTO(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
