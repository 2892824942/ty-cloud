package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.manager.EncryptorManager;
import com.ty.mid.framework.encrypt.mvc.EncryptionHandlerMethodArgumentResolver;
import com.ty.mid.framework.encrypt.mvc.EncryptionParserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@AutoConfigureAfter(EncryptorAutoConfiguration.class)
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
