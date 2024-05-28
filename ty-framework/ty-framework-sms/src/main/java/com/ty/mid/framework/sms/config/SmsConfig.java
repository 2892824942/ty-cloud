package com.ty.mid.framework.sms.config;

import com.ty.mid.framework.core.config.AbstractConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(SmsConfig.PREFIX)
@Validated
@Data
public class SmsConfig extends AbstractConfig {
    public static final String PREFIX = FRAMEWORK_PREFIX + "sms";

    /**
     * 是否开启mock短信能力,开启后,mock短信将走本地mock实现,而不是真正发送短信
     */
    private boolean enableMock;

    /**
     * 需开启enableMock后有效
     * 可开启mock的白名单,默认为空,即:所有用户均为白名单
     * 如配置,则仅白名单中的用户使用mock能力,其他正常走厂商发短信(主要用于非生产环境)
     */
    private String[] whiteList = new String[]{};

    /**
     * 开启框架验证验证码能力,开启后
     * 1.框架会尝试将sms4j提供的SmsDao(默认为本地缓存)重载为分布式缓存
     * 2.如配置,则仅白名单中的用户使用mock能力,其他正常走厂商发短信(主要用于非生产环境)
     */
    private boolean enableValidCode;
}
