package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.common.constant.WebFilterOrderEnum;
import com.ty.mid.framework.core.config.AbstractConfig;
import com.ty.mid.framework.web.core.filter.ApiAccessLogFilter;
import com.ty.mid.framework.web.config.WebConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

@AutoConfigureAfter(WebAutoConfiguration.class)
public class ApiLogAutoConfiguration {

    /**
     * 创建 ApiAccessLogFilter Bean，记录 API 请求日志
     */
    @Bean
    @ConditionalOnProperty(prefix = AbstractConfig.FRAMEWORK_PREFIX + "accessLog", value = "enable", matchIfMissing = true)
    // 允许使用 yudao.access-log.enable=false 禁用访问日志
    public FilterRegistrationBean<ApiAccessLogFilter> apiAccessLogFilter(WebConfig webConfig,
                                                                         @Value("${spring.application.name}") String applicationName) {
        ApiAccessLogFilter filter = new ApiAccessLogFilter(webConfig, applicationName);
        return createFilterBean(filter, WebFilterOrderEnum.API_ACCESS_LOG_FILTER);
    }

    private static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

}
