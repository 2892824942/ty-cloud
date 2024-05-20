package com.ty.mid.framework.web.core.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class BaseTreeVO<C extends BaseVO> extends BaseVO {

    private Collection<C> children = new ArrayList<>(0);

}
