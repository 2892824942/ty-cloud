package com.ty.mid.framework.common.dto;

import lombok.Data;

@Data
public class SimpleTreeDTO extends BaseTreeDTO<SimpleTreeDTO> {

    private String label;

    private String value;

}
