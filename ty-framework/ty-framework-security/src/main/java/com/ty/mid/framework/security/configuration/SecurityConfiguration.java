package com.ty.mid.framework.security.configuration;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.ty.mid.framework.common.constant.DomainConstant;
import com.ty.mid.framework.security.config.SecurityConfig;
import com.ty.mid.framework.security.core.interceptor.UserGuiseInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 权限安全配置
 *
 * @author Lion Li
 */
@EnableConfigurationProperties(SecurityConfig.class)
@ConditionalOnProperty(prefix = SecurityConfig.PREFIX, name = "enable", matchIfMissing = true)
public class SecurityConfiguration implements WebMvcConfigurer {
    @Resource
    private SecurityConfig securityConfig;

    /**
     * 注册sa-token的拦截器
     * 默认使用路由+注解拦截
     * 1.路由负责剔除无需登录的uri
     * 2.注解负责细化,当然,仍然可以注解标注跳过登录
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义认证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 登录校验 -- 拦截所有路由，并排除excludePattern用于开放登录
            SaRouter.match("/**")
                    .notMatch(securityConfig.getExcludePattern())
                    .notMatch(DomainConstant.System.DEFAULT_EXCLUDE_URI)
                    .check(r -> StpUtil.checkLogin());
        }).isAnnotation(securityConfig.isEnableAnnotation())).addPathPatterns("/**");
        if (securityConfig.isEnableGuise()){
            //全局用户伪装
            registry.addInterceptor(new UserGuiseInterceptor(securityConfig));
        }
    }

    /**
     * 校验是否从网关转发
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                .addExclude(DomainConstant.System.DEFAULT_EXCLUDE_URI)
                .setAuth(obj -> {
                    if (SaManager.getConfig().getCheckSameToken()) {
                        SaSameUtil.checkCurrentRequestToken();
                    }
                })
                .setError(e -> SaResult.error("认证失败，无法访问系统资源").setCode(HttpStatus.UNAUTHORIZED.value()));
    }

}
