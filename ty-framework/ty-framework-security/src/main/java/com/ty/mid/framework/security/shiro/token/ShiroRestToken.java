package com.ty.mid.framework.security.shiro.token;

import com.ty.mid.framework.security.shiro.ShiroToken;
import com.ty.mid.framework.security.token.RestToken;

public class ShiroRestToken extends RestToken implements ShiroToken {

    public ShiroRestToken(String token) {
        super(token);
    }

    @Override
    public Object getPrincipal() {
        return this.getToken();
    }

    @Override
    public Object getCredentials() {
        return getToken();
    }
}
