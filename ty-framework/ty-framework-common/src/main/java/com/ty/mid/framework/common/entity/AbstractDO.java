package com.ty.mid.framework.common.entity;

import java.io.Serializable;

public abstract class AbstractDO<ID extends Serializable> {

    public abstract ID getId();

    public abstract void setId(ID id);

}
