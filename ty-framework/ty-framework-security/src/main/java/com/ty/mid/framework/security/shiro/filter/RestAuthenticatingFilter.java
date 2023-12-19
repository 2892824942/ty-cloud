package com.ty.mid.framework.security.shiro.filter;

import com.ty.mid.framework.core.config.CorsConfiguration;
import com.ty.mid.framework.core.config.SecurityConfiguration;
import com.ty.mid.framework.security.RestTokenParser;
import com.ty.mid.framework.security.shiro.token.ShiroRestToken;
import com.ty.mid.framework.security.shiro.token.ShiroUsernamePasswordToken;
import com.ty.mid.framework.security.shiro.util.CorsUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestAuthenticatingFilter extends AuthenticatingFilter {

    private RestTokenParser<HttpServletRequest> restTokenParser;

    private SecurityConfiguration securityConfiguration;
    private CorsConfiguration corsConfiguration;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = WebUtils.toHttp(request);
        if (isLoginRequest(request, response)) {
            String account = req.getParameter(securityConfiguration.getAccountParameterName());
            String password = req.getParameter(securityConfiguration.getPasswordParameterName());
            String captcha = req.getParameter(securityConfiguration.getCaptchaParameterName());
            Boolean isRememberMe = Boolean.valueOf(req.getParameter(securityConfiguration.getRememberMeParameterName()));
            return new ShiroUsernamePasswordToken(account, password);
        }

        if (isLoggedAttempt(request, response)) {
            return createRestToken(req);
        }

        return new UsernamePasswordToken();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // 跨域处理
        CorsUtils.setCorsHeaderIfEnabled(WebUtils.toHttp(response), this.corsConfiguration);

        boolean loggedIn = false;

        if (isLoginRequest(request, response) || isLoggedAttempt(request, response)) {
            loggedIn = executeLogin(request, response);
        }

        if (!loggedIn) {
            HttpServletResponse httpResponse = WebUtils.toHttp(response);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return loggedIn;
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", this.corsConfiguration.getAllowOrigin());
        response.setHeader("Access-Control-Allow-Headers", this.corsConfiguration.getAllowHeaders());
        response.setHeader("Access-Control-Allow-Methods", this.corsConfiguration.getAllowMethods());
        response.setHeader("Access-Control-Allow-Credentials", this.corsConfiguration.getAllowCredentials());
        response.setHeader("Access-Control-Expose-Headers", this.corsConfiguration.getAllowExposeHeaders());
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {

        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        return false;
    }

    protected boolean isLoggedAttempt(ServletRequest request, ServletResponse response) {
        String authzHeader = restTokenParser.parseToken(WebUtils.toHttp(request));
        return authzHeader != null;
    }

    protected AuthenticationToken createRestToken(HttpServletRequest request) {
        String token = this.restTokenParser.parseToken(request);
        return new ShiroRestToken(token);
    }

    public void setRestTokenParser(RestTokenParser<HttpServletRequest> restTokenParser) {
        this.restTokenParser = restTokenParser;
    }

    public void setCorsConfiguration(CorsConfiguration corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }
}
