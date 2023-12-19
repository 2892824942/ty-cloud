package com.ty.mid.framework.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "framework.security")
public class SecurityConfiguration {

    private final Aes aes = new Aes();
    private final Jwt jwt = new Jwt();
    private String cacheKeyPrefix = "auth:token:";
    private long tokenValidity = 600;
    private TimeUnit tokenValidityTimeUnit = TimeUnit.MINUTES;
    private String accountParameterName = "account";
    private String passwordParameterName = "password";
    private String captchaParameterName = "captcha";
    private String rememberMeParameterName = "isRememberMe";
    private Boolean randomToken = Boolean.TRUE;

    public String getCacheKeyPrefix() {
        return cacheKeyPrefix;
    }

    public void setCacheKeyPrefix(String cacheKeyPrefix) {
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    public long getTokenValidity() {
        return tokenValidity;
    }

    public void setTokenValidity(long tokenValidity) {
        this.tokenValidity = tokenValidity;
    }

    public TimeUnit getTokenValidityTimeUnit() {
        return tokenValidityTimeUnit;
    }

    public void setTokenValidityTimeUnit(TimeUnit tokenValidityTimeUnit) {
        this.tokenValidityTimeUnit = tokenValidityTimeUnit;
    }

    public String getAccountParameterName() {
        return accountParameterName;
    }

    public void setAccountParameterName(String accountParameterName) {
        this.accountParameterName = accountParameterName;
    }

    public String getPasswordParameterName() {
        return passwordParameterName;
    }

    public void setPasswordParameterName(String passwordParameterName) {
        this.passwordParameterName = passwordParameterName;
    }

    public String getCaptchaParameterName() {
        return captchaParameterName;
    }

    public void setCaptchaParameterName(String captchaParameterName) {
        this.captchaParameterName = captchaParameterName;
    }

    public String getRememberMeParameterName() {
        return rememberMeParameterName;
    }

    public void setRememberMeParameterName(String rememberMeParameterName) {
        this.rememberMeParameterName = rememberMeParameterName;
    }

    public Boolean getRandomToken() {
        return randomToken;
    }

    public void setRandomToken(Boolean randomToken) {
        this.randomToken = randomToken;
    }

    public Aes getAes() {
        return aes;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public static class Aes {
        private String aesKey;

        public String getAesKey() {
            return aesKey;
        }

        public void setAesKey(String aesKey) {
            this.aesKey = aesKey;
        }
    }

    public static class Jwt {


    }

}
