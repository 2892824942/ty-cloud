package com.ty.mid.framework.common.constant;

/**
 * Web 过滤器顺序的枚举类，保证过滤器按照符合我们的预期 <p>
 * 考虑到每个 starter 都需要用到该工具类，所以放到 common 模块下的 enum 包下 <p>
 *
 * @author suyouliang
 */
public interface WebFilterOrderEnum {
    //全局servlet级异常拦截,优先级最高,兜底filter层处理异常
    int SERVLET_EXCEPTION_FILTER = Integer.MIN_VALUE;
    int CORS_FILTER = Integer.MIN_VALUE + 10;

    int TRACE_FILTER = CORS_FILTER + 1;

    int ENV_TAG_FILTER = TRACE_FILTER + 1;

    int REQUEST_BODY_CACHE_FILTER = Integer.MIN_VALUE + 500;

    // OrderedRequestContextFilter 默认为 -105，用于国际化上下文等等

    int TENANT_CONTEXT_FILTER = -104; // 需要保证在 ApiAccessLogFilter 前面

    int API_ACCESS_LOG_FILTER = -103; // 需要保证在 RequestBodyCacheFilter 后面

    int XSS_FILTER = -102;  // 需要保证在 RequestBodyCacheFilter 后面

    // Spring Security Filter 默认为 -100，可见 org.springframework.boot.autoconfigure.security.SecurityProperties 配置属性类

    int TENANT_SECURITY_FILTER = -99; // 需要保证在 Spring Security 过滤器后面

    int FLOWABLE_FILTER = -98; // 需要保证在 Spring Security 过滤后面

    int DEMO_FILTER = Integer.MAX_VALUE;

}
