package com.ty.mid.framework.security.config;

import com.ty.mid.framework.core.config.AbstractConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.ty.mid.framework.security.config.SecurityConfig.PREFIX;

/**
 * Created by suyouliang on 2020/03/21.
 */
@ConfigurationProperties(prefix = PREFIX)
@Data
public class SecurityConfig extends AbstractConfig {

    public static final String PREFIX = FRAMEWORK_PREFIX + "security";
    /**
     * 是否需要开启security配置
     * 设置为false后,框架将不再注册saToken相关拦截器,以下参数将不再有意义,相关拦截器需要开发者自己实现
     * 适用于
     * 1.默认注册的拦截器已经无法满足业务的复杂度,需要改造的情况
     * 2.需要临时去除登录校验的场景(对于强依赖登录信息的接口仍然无法正常访问)
     * 重写可参考
     * @see com.ty.mid.framework.security.configuration.SecurityConfiguration
     */
    private boolean enable = true;
    /**
     * 需要排除掉登录校验的接口uri 正则path列表
     * 示例:/user/login,/user/**
     */
    private String[] excludePattern = new String[]{};


    /**
     * 是否关闭注解鉴权能力,关闭后框架就只会做路由拦截校验
     * 开启则路由鉴权及注解鉴权均生效
     */
    private boolean isAnnotation = true;


}
