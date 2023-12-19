package com.ty.mid.framework.security.shiro.token;

import com.ty.mid.framework.security.shiro.ShiroToken;
import com.ty.mid.framework.security.token.VerifyCodeToken;

public class ShiroVerifyCodeToken extends VerifyCodeToken implements ShiroToken {

    public ShiroVerifyCodeToken() {
    }

    public ShiroVerifyCodeToken(String account, String verityCode) {
        super(account, verityCode);
    }

    @Override
    public Object getPrincipal() {
        return super.getAccount();
    }

    @Override
    public Object getCredentials() {
        return super.getVerityCode();
    }
}
