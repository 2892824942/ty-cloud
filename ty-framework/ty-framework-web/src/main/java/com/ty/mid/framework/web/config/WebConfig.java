package com.ty.mid.framework.web.config;

import com.ty.mid.framework.common.constant.BooleanEnum;
import com.ty.mid.framework.core.config.AbstractConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

import static com.ty.mid.framework.web.config.WebConfig.PREFIX;

@ConfigurationProperties(PREFIX)
@Validated
@Data
public class WebConfig extends AbstractConfig {
    public static final String PREFIX = FRAMEWORK_PREFIX + "web";
    /**
     * 示例 new Api("/app-api", "**.controller.app.**");
     * 和context-path path类似,不过context-path是整个项目级别的,这个配置是自定义包级别统一加前缀
     * 坏处:对应的前缀无法通过代码搜索获取,搜索的时候需要去除api前缀
     *
     * @return
     */

    private Map<String, Api> customApi = new HashMap<>();


    private boolean enableApiLog;

    private boolean enableCors;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Valid
    public static class Api {

        /**
         * API 前缀，实现所有 Controller 提供的 RESTFul API 的统一前缀
         * <p>
         * <p>
         * 意义：通过该前缀，避免 Swagger、Actuator 意外通过 Nginx 暴露出来给外部，带来安全性问题
         * 这样，Nginx 只需要配置转发到 /api/* 的所有接口即可。
         */
        @NotEmpty(message = "API 前缀不能为空")
        private String prefix;

        /**
         * Controller 所在包的 Ant 路径规则
         * <p>
         * 主要目的是，给该 Controller 设置指定的 {@link #prefix}
         */
        @NotEmpty(message = "Controller 所在包不能为空")
        private String controller;

    }

}
