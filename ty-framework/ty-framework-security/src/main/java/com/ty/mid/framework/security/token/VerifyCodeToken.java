package com.ty.mid.framework.security.token;

import com.ty.mid.framework.security.AuthToken;
import lombok.Data;

@Data
public class VerifyCodeToken implements AuthToken {

    private String account;

    private String verityCode;

    public VerifyCodeToken() {
    }

    public VerifyCodeToken(String account, String verityCode) {
        this.account = account;
        this.verityCode = verityCode;
    }

    @Override
    public String getUserLogin() {
        return account;
    }
}
