package com.ty.mid.framework.security.parser;

import com.ty.mid.framework.security.RestTokenParser;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractRestTokenParser implements RestTokenParser<HttpServletRequest> {

    protected String tokenName;

    public AbstractRestTokenParser(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }
}
