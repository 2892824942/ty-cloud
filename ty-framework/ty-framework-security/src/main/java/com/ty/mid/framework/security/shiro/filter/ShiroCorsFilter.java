package com.ty.mid.framework.security.shiro.filter;

import com.ty.mid.framework.core.config.CorsConfiguration;
import com.ty.mid.framework.security.shiro.util.CorsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

@Slf4j
public class ShiroCorsFilter extends PathMatchingFilter {

    private CorsConfiguration corsConfiguration;

    @Override
    protected boolean onPreHandle(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue) throws Exception {
        boolean corsEnabled = CorsUtils.setCorsHeaderIfEnabled(WebUtils.toHttp(servletResponse), this.corsConfiguration);
        return corsEnabled ? !"OPTIONS".equalsIgnoreCase(WebUtils.toHttp(servletRequest).getMethod()) : true;
    }

    public CorsConfiguration getCorsConfiguration() {
        return corsConfiguration;
    }

    public void setCorsConfiguration(CorsConfiguration corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }
}
