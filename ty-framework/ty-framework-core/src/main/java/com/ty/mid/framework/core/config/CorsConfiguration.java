package com.ty.mid.framework.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "framework.cors")
public class CorsConfiguration {

    private boolean enable = false;

    private String allowOrigin = "*";

    private String allowHeaders = "Content-Type,token";

    private String allowMethods = "GET,PUT,DELETE,POST,OPTIONS";

    private String allowCredentials = "*";

    private String allowExposeHeaders = "token";

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getAllowOrigin() {
        return allowOrigin;
    }

    public void setAllowOrigin(String allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    public String getAllowHeaders() {
        return allowHeaders;
    }

    public void setAllowHeaders(String allowHeaders) {
        this.allowHeaders = allowHeaders;
    }

    public String getAllowMethods() {
        return allowMethods;
    }

    public void setAllowMethods(String allowMethods) {
        this.allowMethods = allowMethods;
    }

    public String getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(String allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public String getAllowExposeHeaders() {
        return allowExposeHeaders;
    }

    public void setAllowExposeHeaders(String allowExposeHeaders) {
        this.allowExposeHeaders = allowExposeHeaders;
    }
}
