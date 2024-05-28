package com.ty.mid.framework.web.core.handler;

import com.ty.mid.framework.common.pojo.BaseResult;
import com.ty.mid.framework.common.pojo.Result;
import com.ty.mid.framework.web.core.filter.ApiAccessLogFilter;
import com.ty.mid.framework.web.core.util.WebFrameworkUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 全局响应结果（ResponseBody）处理器 <p>
 * 目前，GlobalResponseBodyHandler 的主要作用是，记录 Controller 的返回结果， <p>
 * 方便 {@link ApiAccessLogFilter} 记录访问日志
 */
@ControllerAdvice
public class ControllerResponseBodyHandler implements ResponseBodyAdvice<Object> {

    @Override
    @SuppressWarnings("NullableProblems") // 避免 IDEA 警告
    public boolean supports(MethodParameter returnType, Class converterType) {
        if (returnType.getMethod() == null) {
            return false;
        }
        // 只拦截返回结果为 Result及其子类型
        return Result.class.isAssignableFrom(returnType.getMethod().getReturnType());
    }

    @Override
    @SuppressWarnings("NullableProblems") // 避免 IDEA 警告
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        resetResponse();
        // 记录 Controller 结果
        WebFrameworkUtils.setCommonResult(((ServletServerHttpRequest) request).getServletRequest(), (BaseResult<?>) body);
        return body;
    }


    private void resetResponse() {
        // 获取当前请求的 ServletRequestAttributes
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            // 通过 ServletRequestAttributes 获取 HttpServletResponse
            HttpServletResponse servletResponse = attributes.getResponse();
            if (Objects.nonNull(servletResponse)) {
                servletResponse.resetBuffer();
            }
        }
    }

}
