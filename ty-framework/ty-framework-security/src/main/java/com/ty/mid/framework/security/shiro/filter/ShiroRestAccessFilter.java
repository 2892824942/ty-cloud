package com.ty.mid.framework.security.shiro.filter;

import com.ty.mid.framework.security.RestTokenParser;
import com.ty.mid.framework.security.shiro.token.ShiroRestToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ShiroRestAccessFilter extends AccessControlFilter {

    private static Logger log = LoggerFactory.getLogger(ShiroRestAccessFilter.class);

    private RestTokenParser tokenParser;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        if (com.ty.mid.framework.security.shiro.util.SecurityUtils.hasLogin())
            return true;

        String token = tokenParser.parseToken(WebUtils.toHttp(request));
        if (StringUtils.hasText(token)) {
            ShiroRestToken restToken = new ShiroRestToken(token);
            try {
                SecurityUtils.getSubject().login(restToken);
            } catch (AuthenticationException e) {
                log.debug("try login error", e);
            }
        }

        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return true;
    }

    public RestTokenParser getTokenParser() {
        return tokenParser;
    }

    public void setTokenParser(RestTokenParser tokenParser) {
        this.tokenParser = tokenParser;
    }
}
