package com.ty.mid.framework.security;

public interface RestTokenParser<T> {

    String parseToken(T tokenIn);

}
