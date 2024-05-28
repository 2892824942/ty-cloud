package com.ty.mid.framework.web.core.filter;

import com.ty.mid.framework.core.util.servlet.ServletUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Request Body 缓存 Filter，实现它的可重复读取 <p>
 *
 * @author suyouliang
 */
public class CacheRequestBodyFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (ServletUtils.isJsonRequest(request)) {
            filterChain.doFilter(new CacheRequestBodyWrapper(request), response);
        } else {
            filterChain.doFilter(request, response);
        }

    }

}
