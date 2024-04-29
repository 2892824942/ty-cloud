package com.ty.mid.framework.web.xss.core.filter;

import com.ty.mid.framework.web.config.WebConfig;
import com.ty.mid.framework.web.xss.core.clean.XssCleaner;
import lombok.AllArgsConstructor;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Xss 过滤器 <p>
 * @author suyouliang
 */
@AllArgsConstructor
public class XssFilter extends OncePerRequestFilter {

    /**
     * 属性
     */
    private final WebConfig webConfig;
    /**
     * 路径匹配器
     */
    private final PathMatcher pathMatcher;

    private final XssCleaner xssCleaner;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        filterChain.doFilter(new XssRequestWrapper(request, xssCleaner), response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 如果关闭，则不过滤
        WebConfig.Xss xss = webConfig.getXss();
        if (!xss.isEnable()) {
            return true;
        }

        // 如果匹配到无需过滤，则不过滤
        String uri = request.getRequestURI();
        return xss.getExcludeUrls().stream().anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, uri));
    }

}
