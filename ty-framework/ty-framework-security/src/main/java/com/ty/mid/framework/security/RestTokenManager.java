package com.ty.mid.framework.security;

import java.util.Map;

/**
 * @author suyouliang
 * @createTime 2019-07-07 09:40
 */
public interface RestTokenManager<T, A extends AuthToken> extends RestTokenValidator, RestTokenPersister<A> {

    RestTokenParser<T> getTokenParser();

    String generateToken(Map<String, ?> tokenData);

    String generateToken(A tokenData);

    Map<String, ?> decodeToken(String token);

    A decodeSureToken(String token);
}
