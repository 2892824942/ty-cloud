package com.ty.mid.framework.security;

import java.util.Map;

public interface TokenGenerator {

    String generateToken(Map<String, ?> tokenData);

    <A extends AuthToken> String generateSureToken(A tokenData);

    <A extends AuthToken> A decodeSureToken(String token);

    Map<String, ?> decodeToken(String token);
}
