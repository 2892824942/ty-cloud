package com.ty.mid.framework.autoconfigure;

import cn.hutool.core.collection.CollUtil;
import com.ty.mid.framework.web.config.WebConfig;
import com.ty.mid.framework.web.swagger.config.SwaggerConfig;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * 用来根据配置动态像spring容器中添加bean,暂时留着
 */
public class EnvDynamicBeanRegister implements ImportBeanDefinitionRegistrar, EnvironmentPostProcessor {
    private ConfigurableEnvironment environment;

    private SwaggerConfig swaggerConfig;
    private WebConfig webConfig;

    /**
     * 自定义模块的 API 分组
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        MutablePropertySources propertySources = environment.getPropertySources();
        Map<String, WebConfig.Api> customApiMap = environment.getProperty(WebConfig.PREFIX + ".custom-api", Map.class);
        if (CollUtil.isEmpty(customApiMap)) {
            return;
        }
        customApiMap.forEach((k, v) -> {
                    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                    beanDefinition.setBeanClass(GroupedOpenApi.class);
                    beanDefinition.getPropertyValues().add("group", k);
                    beanDefinition.getPropertyValues().add("pathsToMatch", v.getPrefix() + "/**");
                    registry.registerBeanDefinition(k, beanDefinition);
                }
        );
    }


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (this.environment == null) {
            this.environment = environment;
        }
    }
}

