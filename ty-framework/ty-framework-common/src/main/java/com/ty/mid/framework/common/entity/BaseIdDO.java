package com.ty.mid.framework.common.entity;

import java.io.Serializable;

public interface BaseIdDO<ID extends Serializable> extends Serializable {
    ID getId();


}
