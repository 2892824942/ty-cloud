package com.ty.mid.framework.common.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LabelValueDTO extends DropdownDTO<String, String> {

    public LabelValueDTO(String label, String value) {
        super(label, value);
    }

}
