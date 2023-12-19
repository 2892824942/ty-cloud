package com.ty.mid.framework.cache.model;

import org.springframework.cache.Cache.ValueWrapper;

import java.io.Serializable;

/**
 * @author 苏友良
 */
public class NullValue implements ValueWrapper, Serializable {

    public static final NullValue INSTANCE = new NullValue();
    private static final long serialVersionUID = -8310337775544536701L;

    @Override
    public Object get() {
        return null;
    }

}
