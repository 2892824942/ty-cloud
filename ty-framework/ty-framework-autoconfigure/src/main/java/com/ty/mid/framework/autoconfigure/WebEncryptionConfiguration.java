package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.common.constant.WebFilterOrderEnum;
import com.ty.mid.framework.core.bus.EventPublisher;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.manager.EncryptorManager;
import com.ty.mid.framework.encrypt.mvc.EncryptionHandlerMethodArgumentResolver;
import com.ty.mid.framework.encrypt.mvc.EncryptionParserConverter;
import com.ty.mid.framework.web.config.WebConfig;
import com.ty.mid.framework.web.core.filter.ApiAccessLogFilter;
import com.ty.mid.framework.web.core.filter.CacheRequestBodyFilter;
import com.ty.mid.framework.web.core.filter.GlobalServletExceptionHandleFilter;
import com.ty.mid.framework.web.core.handler.ControllerExceptionHandler;
import com.ty.mid.framework.web.core.handler.ControllerResponseBodyHandler;
import com.ty.mid.framework.web.core.service.ApiLogService;
import com.ty.mid.framework.web.core.util.WebUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
import java.util.Objects;

@RequiredArgsConstructor
@ConditionalOnClass(EncryptorManager.class)
@ConditionalOnBean(EncryptorManager.class)
@ConditionalOnProperty(prefix = EncryptorConfig.PREFIX, name = "enable", havingValue = "true")
public class WebEncryptionConfiguration implements WebMvcConfigurer {

    private final ObjectProvider<EncryptorManager> encryptorManagerObjectProvider;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        EncryptorManager encryptorManager = encryptorManagerObjectProvider.getIfAvailable();
        if (Objects.isNull(encryptorManager)) {
            return;
        }
        resolvers.add(new EncryptionHandlerMethodArgumentResolver(encryptorManager));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        EncryptorManager encryptorManager = encryptorManagerObjectProvider.getIfAvailable();
        if (Objects.isNull(encryptorManager)) {
            return;
        }
        //registry.addFormatterForFieldAnnotation(new HashIdFormatter(encryptorManager));
        registry.addConverter(new EncryptionParserConverter(encryptorManager));

    }
}
