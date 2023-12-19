package com.ty.mid.framework.security.parser;

import javax.servlet.http.HttpServletRequest;

public class BodyRestTokenParser extends AbstractRestTokenParser {

    public BodyRestTokenParser(String tokenName) {
        super(tokenName);
    }

    @Override
    public String parseToken(HttpServletRequest request) {
        return request.getParameter(super.getTokenName());
    }
}
