package com.ty.mid.framework.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class BaseTreeDTO<C extends BaseDTO> extends BaseDTO {

    private Collection<C> children = new ArrayList<>(0);

}
