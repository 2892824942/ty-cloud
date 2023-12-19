package com.ty.mid.framework.security;

public interface RestTokenValidator {

    boolean validateToken(String token);

}
