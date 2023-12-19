package com.ty.mid.framework.security.parser;

import javax.servlet.http.HttpServletRequest;

public class HeaderRestTokenParser extends AbstractRestTokenParser {
    public HeaderRestTokenParser(String tokenName) {
        super(tokenName);
    }

    @Override
    public String parseToken(HttpServletRequest request) {
        return request.getHeader(super.tokenName);
    }
}
