package com.ty.mid.framework.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class DropdownDTO<T, V> extends AbstractDTO {

    private T label;

    private V value;

    private Boolean checked = Boolean.FALSE;

    public DropdownDTO(T label, V value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DropdownDTO that = (DropdownDTO) o;
        return label.equals(that.label) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, value);
    }
}
