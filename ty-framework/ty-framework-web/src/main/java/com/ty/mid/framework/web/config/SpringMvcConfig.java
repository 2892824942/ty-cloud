package com.ty.mid.framework.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ty.mid.framework.web.annotation.MvcInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer, ApplicationContextAware {

    private ApplicationContext context;

    @Bean
    @ConditionalOnMissingBean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Collection<Object> interceptors = this.context.getBeansWithAnnotation(MvcInterceptor.class).values();
        for (Object interceptor : interceptors) {
            registry.addInterceptor((HandlerInterceptor) interceptor);
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
