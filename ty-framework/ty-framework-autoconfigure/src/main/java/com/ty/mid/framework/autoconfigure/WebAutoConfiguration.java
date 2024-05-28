package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.common.constant.WebFilterOrderEnum;
import com.ty.mid.framework.core.bus.EventPublisher;
import com.ty.mid.framework.web.config.WebConfig;
import com.ty.mid.framework.web.core.filter.ApiAccessLogFilter;
import com.ty.mid.framework.web.core.filter.CacheRequestBodyFilter;
import com.ty.mid.framework.web.core.filter.GlobalServletExceptionHandleFilter;
import com.ty.mid.framework.web.core.handler.ControllerExceptionHandler;
import com.ty.mid.framework.web.core.handler.ControllerResponseBodyHandler;
import com.ty.mid.framework.web.core.service.ApiLogService;
import com.ty.mid.framework.web.core.util.WebUtil;
import com.ty.mid.framework.web.mvc.HashedIdFieldFormatter;
import com.ty.mid.framework.web.mvc.HashedIdHandlerMethodArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@EnableConfigurationProperties(WebConfig.class)
@RequiredArgsConstructor
public class WebAutoConfiguration implements WebMvcConfigurer {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher(".");
    private final WebConfig webConfig;
    /**
     * 应用名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        if (!webConfig.isEnableMvcUrlPrefix()) {
            return;
        }
        Map<String, WebConfig.Api> customApi = webConfig.getCustomApi();
        if (CollectionUtils.isEmpty(customApi)) {
            return;
        }
        //为定义的每个API设置前缀
        customApi.forEach((k, v) -> configurePathMatch(configurer, v));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        if (webConfig.getHashId().isEnable()) {
            resolvers.add(new HashedIdHandlerMethodArgumentResolver(webConfig));
        }
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        if (webConfig.getHashId().isEnable()) {
            registry.addFormatterForFieldAnnotation(new HashedIdFieldFormatter(webConfig));
        }

    }

    /**
     * 设置 API 前缀，仅仅匹配 controller 包下的
     *
     * @param configurer 配置
     * @param api        API 配置
     */
    private void configurePathMatch(PathMatchConfigurer configurer, WebConfig.Api api) {
        if (!api.isEnableMvcUrlPrefix()) {
            return;
        }

        configurer.addPathPrefix(api.getPrefix()
                , clazz -> (clazz.isAnnotationPresent(RestController.class) || clazz.isAnnotationPresent(Controller.class))
                        && pathMatch(ANT_PATH_MATCHER, api, clazz));
    }

    /**
     * 匹配controller 包
     *
     * @param antPathMatcher
     * @param api
     * @param clazz
     * @return
     */
    private boolean pathMatch(AntPathMatcher antPathMatcher, WebConfig.Api api, Class<?> clazz) {
        return Arrays.stream(api.getController()).anyMatch(controller -> antPathMatcher.match(controller, clazz.getPackage().getName()));
    }

    @Bean
    public ApiLogService apiLogService(EventPublisher eventPublisher) {
        return new ApiLogService(eventPublisher);
    }

    @Bean
    public ControllerExceptionHandler controllerExceptionHandler(ApiLogService apiLogService) {
        return new ControllerExceptionHandler(applicationName, apiLogService);
    }

    @Bean
    public ControllerResponseBodyHandler controllerResponseBodyHandler() {
        return new ControllerResponseBodyHandler();
    }

    // ========== Filter 相关 ==========

    /**
     * 创建 ApiAccessLogFilter Bean，记录 API 请求日志
     */
    @Bean
    @ConditionalOnProperty(prefix = WebConfig.PREFIX, name = "api-log.enable", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<ApiAccessLogFilter> apiAccessLogFilter(WebConfig webConfig, ApiLogService apiLogService,
                                                                         @Value("${spring.application.name}") String applicationName) {
        ApiAccessLogFilter filter = new ApiAccessLogFilter(webConfig, apiLogService, applicationName);
        return WebUtil.createFilterBean(filter, WebFilterOrderEnum.API_ACCESS_LOG_FILTER);
    }

    /**
     * 创建 CorsFilter Bean，解决跨域问题
     */
    @Bean
    @ConditionalOnProperty(prefix = WebConfig.PREFIX, name = "enable-cors", havingValue = "true")
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        // 创建 CorsConfiguration 对象
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 设置访问源地址
        config.addAllowedHeader("*"); // 设置访问源请求头
        config.addAllowedMethod("*"); // 设置访问源请求方法
        // 创建 UrlBasedCorsConfigurationSource 对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 对接口配置跨域设置
        return WebUtil.createFilterBean(new CorsFilter(source), WebFilterOrderEnum.CORS_FILTER);
    }


    /**
     * 创建全局异常处理 Filter,此异常处理器处理GlobalExceptionHandler处理不了的异常
     */
    @Bean
    @ConditionalOnProperty(prefix = WebConfig.PREFIX, name = "enable-servlet-exception-handle", havingValue = "true")
    public FilterRegistrationBean<GlobalServletExceptionHandleFilter> globalServletExceptionFilter(ControllerExceptionHandler controllerExceptionHandler) {
        // 创建 CorsConfiguration 对象
        return WebUtil.createFilterBean(new GlobalServletExceptionHandleFilter(controllerExceptionHandler), WebFilterOrderEnum.SERVLET_EXCEPTION_FILTER);
    }

    /**
     * 创建 RequestBodyCacheFilter Bean，可重复读取请求内容
     */
    @Bean
    public FilterRegistrationBean<CacheRequestBodyFilter> requestBodyCacheFilter() {
        return WebUtil.createFilterBean(new CacheRequestBodyFilter(), WebFilterOrderEnum.REQUEST_BODY_CACHE_FILTER);
    }


    /**
     * 创建 RestTemplate 实例
     *
     * @param restTemplateBuilder {@link RestTemplateAutoConfiguration#restTemplateBuilder}
     */
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
