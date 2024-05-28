package com.ty.mid.framework.security.config;

import com.ty.mid.framework.core.config.AbstractConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.ty.mid.framework.security.config.SecurityConfig.PREFIX;

/**
 * 安全权限配置类 <p>
 * 目前框架默认使用路由+注解权限组合 <p>
 * 最佳实践: <p>
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
     *
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
     * <p>
     * 即:开启伪装后,可通过LoginHelper当前的token伪装成另外一个user,直至关闭
     * <p>
     * 开启则路由鉴权及注解鉴权均生效
     */
    private boolean enableGuise = false;

    /**
     * 可开启token伪装能力userId白名单,默认为空,即:所有用户均为白名单
     * 设置后则仅设置的用户可使用token伪装能力
     * 注意:需开启 enableGuise生效
     * 伪装成另外一个用户和saToken本身的伪装区别:
     * saToken的伪装是请求级别,即开启伪装仅在当前请求内有效(使用lambda则在lambda内有效)
     * 本类:增强了伪装,将伪装生命周期扩展到全局(基于Interceptor实现),一旦开启,将在失效内一直有效,不受请求限制
     * *
     * 使用场景示例:知道用户id的情况下
     * 1.通过自己的账号直接模拟对应用户,去除造token或找token的成本----非生产环境开发测试使用
     * 2.生产环境紧急救援
     * 部分B端业务,某些特定的bug不好复现,可能存在紧急救援或用户授权登录B端企业账号处理问题的场景
     * 注意:使用时慎重!!!!必须保证入口私密,不要在线上直接暴露(应通过授权,密码登方式),否则后果自负
     */
    private String[] enableGuiseUserIds = new String[]{};


}
