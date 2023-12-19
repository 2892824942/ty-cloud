package com.ty.mid.framework.security.shiro.permission;

import com.ty.mid.framework.core.util.StringUtils;
import org.apache.shiro.authz.Permission;

import java.io.Serializable;

public class UrlPatternPermission implements Permission, Serializable {

    private String urlPattern;

    public UrlPatternPermission() {
    }

    public UrlPatternPermission(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    public boolean implies(Permission p) {
        if (StringUtils.isEmpty(urlPattern)) {
            return false;
        }

        String perm = p.toString();

        return perm.matches(urlPattern);
    }
}
