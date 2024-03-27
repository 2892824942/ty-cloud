package com.ty.mid.framework.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ty.mid.framework.common.constant.WebFilterOrderEnum;
import com.ty.mid.framework.web.config.WebConfig;
import com.ty.mid.framework.web.core.util.WebUtil;
import com.ty.mid.framework.web.xss.core.clean.JsoupXssCleaner;
import com.ty.mid.framework.web.xss.core.clean.XssCleaner;
import com.ty.mid.framework.web.xss.core.filter.XssFilter;
import com.ty.mid.framework.web.xss.core.json.XssStringJsonDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@EnableConfigurationProperties(WebConfig.class)
@ConditionalOnProperty(prefix = WebConfig.PREFIX, name = "xss.enable", havingValue = "true", matchIfMissing = true)
// 设置为 false 时，禁用
public class XssAutoConfiguration implements WebMvcConfigurer {

    /**
     * Xss 清理者
     *
     * @return XssCleaner
     */
    @Bean
    @ConditionalOnMissingBean(XssCleaner.class)
    public XssCleaner xssCleaner() {
        return new JsoupXssCleaner();
    }

    /**
     * 注册 Jackson 的序列化器，用于处理 json 类型参数的 xss 过滤
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnMissingBean(name = "xssJacksonCustomizer")
    @ConditionalOnBean(ObjectMapper.class)
    public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(XssCleaner xssCleaner) {
        // 在反序列化时进行 xss 过滤，可以替换使用 XssStringJsonSerializer，在序列化时进行处理
        return builder -> builder.deserializerByType(String.class, new XssStringJsonDeserializer(xssCleaner));
    }

    /**
     * 创建 XssFilter Bean，解决 Xss 安全问题
     */
    @Bean
    @ConditionalOnBean(XssCleaner.class)
    public FilterRegistrationBean<XssFilter> xssFilter(WebConfig webConfig, PathMatcher pathMatcher, XssCleaner xssCleaner) {
        return WebUtil.createFilterBean(new XssFilter(webConfig, pathMatcher, xssCleaner), WebFilterOrderEnum.XSS_FILTER);
    }
}
