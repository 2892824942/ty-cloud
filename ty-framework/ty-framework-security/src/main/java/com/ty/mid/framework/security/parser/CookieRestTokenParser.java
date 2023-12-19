package com.ty.mid.framework.security.parser;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
public class CookieRestTokenParser extends AbstractRestTokenParser {

    public CookieRestTokenParser(String tokenName) {
        super(tokenName);
    }

    @Override
    public String parseToken(HttpServletRequest request) {
        String token = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            Cookie cookie = Arrays.stream(cookies).filter(c -> super.tokenName.equals(c.getName()))
                    .findAny().orElse(null);
            token = cookie != null ? cookie.getValue() : null;
        }

        log.info("parse jwt token from cookie, jwt token: {}", token);

        return token;
    }
}
