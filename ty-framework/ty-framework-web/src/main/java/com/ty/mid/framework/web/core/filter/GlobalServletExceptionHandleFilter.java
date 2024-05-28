package com.ty.mid.framework.web.core.filter;

import com.ty.mid.framework.common.pojo.Result;
import com.ty.mid.framework.core.util.servlet.ServletUtils;
import com.ty.mid.framework.web.core.handler.ControllerExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 优先级最高的filter <p>
 * 此filter拦截filter->controller(不包括)中间所有层出现的异常信息 <p>
 *
 * @author suyouliang
 */
@RequiredArgsConstructor
public class GlobalServletExceptionHandleFilter extends OncePerRequestFilter {
    private final ControllerExceptionHandler globalExceptionHandler;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            Result<?> result = globalExceptionHandler.allExceptionHandler(request, e);
            ServletUtils.writeJSON(response, result);
        }
    }

}
