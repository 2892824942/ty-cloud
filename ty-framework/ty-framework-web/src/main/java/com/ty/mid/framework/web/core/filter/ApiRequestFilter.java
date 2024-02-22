package com.ty.mid.framework.web.core.filter;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.web.config.WebConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 过滤 API 请求的过滤器
 *
 * @author 芋道源码
 */
@RequiredArgsConstructor
public abstract class ApiRequestFilter extends OncePerRequestFilter {

    protected final WebConfig webConfig;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        Map<String, WebConfig.Api> customApi = webConfig.getCustomApi();
        if (CollectionUtils.isEmpty(customApi)) {
            //如果没有定义任何的api前缀,全部拦截
            return Boolean.FALSE;
        }
        // 只拦截定义
        return customApi.entrySet().stream().noneMatch(entry -> StrUtil.startWithAny(request.getRequestURI(), entry.getValue().getPrefix()));

    }

}
