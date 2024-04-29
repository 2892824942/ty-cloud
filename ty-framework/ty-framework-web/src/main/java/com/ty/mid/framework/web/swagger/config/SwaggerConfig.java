package com.ty.mid.framework.web.swagger.config;

import com.ty.mid.framework.core.config.AbstractConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

import static com.ty.mid.framework.core.config.AbstractConfig.FRAMEWORK_PREFIX;

/**
 * Swagger 配置属性 <p>
 * @author suyouliang 
 */
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(FRAMEWORK_PREFIX + "swagger")
@Data
public class SwaggerConfig extends AbstractConfig {

    /**
     * 标题
     */
    @NotEmpty(message = "标题不能为空")
    private String title;
    /**
     * 描述
     */
    @NotEmpty(message = "描述不能为空")
    private String description;
    /**
     * 作者
     */
    @NotEmpty(message = "作者不能为空")
    private String author;
    /**
     * 版本
     */
    @NotEmpty(message = "版本不能为空")
    private String version;
    /**
     * url
     */
    @NotEmpty(message = "扫描的 package 不能为空")
    private String url;
    /**
     * email
     */
    @NotEmpty(message = "扫描的 email 不能为空")
    private String email;

    /**
     * license
     */
    @NotEmpty(message = "扫描的 license 不能为空")
    private String license;

    /**
     * license-url
     */
    @NotEmpty(message = "扫描的 license-url 不能为空")
    private String licenseUrl;

}
