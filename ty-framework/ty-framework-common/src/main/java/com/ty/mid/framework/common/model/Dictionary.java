package com.ty.mid.framework.common.model;

import java.io.Serializable;

public interface Dictionary<T extends Serializable, V extends Serializable> {

    T getCode();

    T getParentCode();

    V getValue();

    String getText();

    String getTextApp();

    Integer getSequence();

}
