package com.ty.mid.framework.starter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ty.mid.framework.cache.support.RedisCache;
import com.ty.mid.framework.core.bus.EventPublisher;
import com.ty.mid.framework.core.config.CorsConfiguration;
import com.ty.mid.framework.core.config.SecurityConfiguration;
import com.ty.mid.framework.security.*;
import com.ty.mid.framework.security.generator.AesTokenGenerator;
import com.ty.mid.framework.security.parser.HeaderRestTokenParser;
import com.ty.mid.framework.security.shiro.ShiroToken;
import com.ty.mid.framework.security.shiro.filter.RestAuthenticatingFilter;
import com.ty.mid.framework.security.shiro.filter.ShiroCorsFilter;
import com.ty.mid.framework.security.shiro.filter.ShiroRestAccessFilter;
import com.ty.mid.framework.security.shiro.support.ShiroRestAuthenticationService;
import com.ty.mid.framework.starter.configuration.manager.DefaultRestTokenManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@Import({CorsConfiguration.class, SecurityConfiguration.class})
public class WebSecurityAutoConfiguration {

    @Autowired
    private CorsConfiguration corsConfiguration;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    Map<String, Filter> shiroFiltersMap() {
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("cors", corsFilter());
        filters.put("access", shiroAccessFilter());
        filters.put("rest", restAuthenticatingFilter());

        return filters;
    }

    @Bean(name = "filterShiroFilterRegistrationBean")
    @ConditionalOnMissingBean
    FilterRegistrationBean<AbstractShiroFilter> filterShiroFilterRegistrationBean(ShiroFilterFactoryBean factory) throws Exception {
        factory.setFilters(shiroFiltersMap());

        FilterRegistrationBean<AbstractShiroFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter((AbstractShiroFilter) Objects.requireNonNull(factory.getObject()));
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST, DispatcherType.INCLUDE);
        return filterRegistrationBean;
    }

    ShiroCorsFilter corsFilter() {
        ShiroCorsFilter corsFilter = new ShiroCorsFilter();
        corsFilter.setCorsConfiguration(corsConfiguration);
        return corsFilter;
    }

    ShiroRestAccessFilter shiroAccessFilter() {
        ShiroRestAccessFilter accessFilter = new ShiroRestAccessFilter();
        accessFilter.setTokenParser(tokenParser());

        return accessFilter;
    }

    RestAuthenticatingFilter restAuthenticatingFilter() {
        RestAuthenticatingFilter filter = new RestAuthenticatingFilter();
        filter.setRestTokenParser(tokenParser());

        return filter;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Bean
    @ConditionalOnMissingBean
    RestTokenParser<HttpServletRequest> tokenParser() {
        return new HeaderRestTokenParser("token");
    }

    @Bean
    @ConditionalOnMissingBean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    TokenGenerator tokenGenerator(ObjectMapper objectMapper) {
        return new AesTokenGenerator(this.securityConfiguration, objectMapper);
    }

    @Bean
    @ConditionalOnClass(RedisCache.class)
    @ConditionalOnMissingBean
    RestTokenManager<HttpServletRequest, ShiroToken> tokenManager(ObjectMapper objectMapper) {
        return new DefaultRestTokenManager(tokenParser(), tokenGenerator(objectMapper), this.securityConfiguration, new RedisCache());
    }


    @Bean
    @ConditionalOnMissingBean
    AuthenticationService<ShiroToken, AuthenticationResult> authenticationService(EventPublisher publisher, RestTokenManager<HttpServletRequest, ShiroToken> tokenManager) {
        ShiroRestAuthenticationService<ShiroToken, AuthenticationResult> ret = new ShiroRestAuthenticationService<>(publisher, tokenManager, this.securityConfiguration.getRandomToken());
        return ret;
    }
}
