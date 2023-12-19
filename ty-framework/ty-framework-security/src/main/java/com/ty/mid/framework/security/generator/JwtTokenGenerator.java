package com.ty.mid.framework.security.generator;

import com.ty.mid.framework.security.AuthToken;
import com.ty.mid.framework.security.TokenGenerator;

import java.util.Map;

public class JwtTokenGenerator implements TokenGenerator {
    @Override
    public String generateToken(Map<String, ?> tokenData) {
        throw new UnsupportedOperationException("暂未实现 JWT token！");
    }

    @Override
    public <A extends AuthToken> String generateSureToken(A tokenData) {
        throw new UnsupportedOperationException("暂未实现 JWT token！");
    }

    @Override
    public <A extends AuthToken> A decodeSureToken(String token) {
        throw new UnsupportedOperationException("暂未实现 JWT token！");
    }

    @Override
    public Map<String, ?> decodeToken(String token) {
        throw new UnsupportedOperationException("暂未实现 JWT token！");

    }
}
