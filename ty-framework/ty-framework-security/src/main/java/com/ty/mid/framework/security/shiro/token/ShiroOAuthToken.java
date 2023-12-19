package com.ty.mid.framework.security.shiro.token;

import com.ty.mid.framework.security.shiro.ShiroToken;
import com.ty.mid.framework.security.token.OAuthToken;

public class ShiroOAuthToken extends OAuthToken implements ShiroToken {
    @Override
    public Object getPrincipal() {
        return getCode();
    }

    @Override
    public Object getCredentials() {
        return getCode();
    }
}
