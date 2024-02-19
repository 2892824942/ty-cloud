package com.ty.mid.framework.web.core.handler;

import com.ty.mid.framework.common.pojo.BaseResult;
import com.ty.mid.framework.web.core.filter.ApiAccessLogFilter;
import com.ty.mid.framework.web.core.util.WebFrameworkUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应结果（ResponseBody）处理器
 * <p>
 * 目前，GlobalResponseBodyHandler 的主要作用是，记录 Controller 的返回结果，
 * 方便 {@link ApiAccessLogFilter} 记录访问日志
 */
@ControllerAdvice
public class GlobalResponseBodyHandler implements ResponseBodyAdvice<Object> {

    @Override
    @SuppressWarnings("NullableProblems") // 避免 IDEA 警告
    public boolean supports(MethodParameter returnType, Class converterType) {
        if (returnType.getMethod() == null) {
            return false;
        }
        // 只拦截返回结果为 CommonResult 类型
        return returnType.getMethod().getReturnType() == BaseResult.class;
    }

    @Override
    @SuppressWarnings("NullableProblems") // 避免 IDEA 警告
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 记录 Controller 结果
        WebFrameworkUtils.setCommonResult(((ServletServerHttpRequest) request).getServletRequest(), (BaseResult<?>) body);
        return body;
    }

}
