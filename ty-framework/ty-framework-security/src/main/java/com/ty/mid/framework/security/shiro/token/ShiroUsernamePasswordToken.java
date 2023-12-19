package com.ty.mid.framework.security.shiro.token;

import com.ty.mid.framework.security.shiro.ShiroToken;
import com.ty.mid.framework.security.token.UsernamePasswordToken;

public class ShiroUsernamePasswordToken extends UsernamePasswordToken implements ShiroToken {

    public ShiroUsernamePasswordToken(String username, String password) {
        super(username, password);
    }

    @Override
    public Object getPrincipal() {
        return this.getUsername();
    }

    @Override
    public Object getCredentials() {
        return this.getPassword();
    }
}
