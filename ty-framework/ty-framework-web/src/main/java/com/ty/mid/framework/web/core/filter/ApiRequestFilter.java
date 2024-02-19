package com.ty.mid.framework.web.core.filter;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.web.config.WebConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * 过滤 /admin-api、/app-api 等 API 请求的过滤器
 *
 * @author 芋道源码
 */
@RequiredArgsConstructor
public abstract class ApiRequestFilter extends OncePerRequestFilter {

    protected final WebConfig webConfig;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 只过滤 API 请求的地址
        return !StrUtil.startWithAny(request.getRequestURI(), webConfig.getAdminApi().getPrefix(),
                webConfig.getAppApi().getPrefix());
    }

}
