package com.ty.mid.framework.web.core.filter;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.web.config.WebConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * 过滤 API 请求的过滤器
 *
 * @author suyouliang
 */
@RequiredArgsConstructor
public abstract class ApiRequestFilter extends OncePerRequestFilter {

    private static final String PATH_SEPARATOR = "/";
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher(PATH_SEPARATOR);
    protected final WebConfig webConfig;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        WebConfig.ApiLog apiLog = webConfig.getApiLog();
        @NotNull String[] excludeUriArray = ArrayUtil.addAll(apiLog.getExcludeUri(), apiLog.getAdditionalExcludeUri());

        return Arrays.stream(excludeUriArray).anyMatch(excludeUri -> {
            //如果配置忘记加上"/",自动加上
            excludeUri = StrUtil.startWith(excludeUri, PATH_SEPARATOR) ? excludeUri : PATH_SEPARATOR.concat(excludeUri);
            return ANT_PATH_MATCHER.match(excludeUri, request.getRequestURI());
        });
    }

}
