package com.ty.mid.framework.security.config;

import com.ty.mid.framework.core.config.AbstractConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.ty.mid.framework.security.config.SecurityConfig.PREFIX;

/**
 * 安全权限配置类
 * 目前框架默认使用路由+注解权限组合
 * 最佳实践:
 * 路由默认过滤uri比较规律的部分接口,注解则处理某些特殊接口
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
     * 是否开启注解鉴权能力,关闭后框架就只会做路由拦截校验
     * 开启则路由鉴权及注解鉴权均生效
     */
    private boolean enableAnnotation = true;

    /**
     * 是否开启token伪装能力,默认为false
     * 即:开启伪装后,可通过LoginHelper当前的token伪装成另外一个user,直至关闭
     * 开启则路由鉴权及注解鉴权均生效
     */
    private boolean enableGuise = false;

    /**
     * 可开启token伪装能力userId白名单,默认为空,即:所有用户均为白名单
     * 设置后则仅设置的用户可使用token伪装能力
     * 注意:需开启 enableGuise生效
     */
    private String[] enableGuiseUserIds = new String[]{};


}
