package com.ty.mid.framework.web.config;

import com.ty.mid.framework.common.constant.DomainConstant;
import com.ty.mid.framework.core.config.AbstractConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ty.mid.framework.web.config.WebConfig.PREFIX;

@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(PREFIX)
@Validated
@Data
public class WebConfig extends AbstractConfig {
    public static final String PREFIX = FRAMEWORK_PREFIX + "web";
    /**
     * 示例 new Api("/app-api", "**.controller.app.**");
     * key:api分组名称,即swagger的api分组名称
     * 当启enableMvcUrlPrefix后,key还会作为包级别统一前缀
     * 和context-path path类似,不过context-path是整个项目级别的,这个配置是自定义包级别统一加前缀
     * 具体
     *
     * @see WebConfig#enableMvcUrlPrefix
     * <p>
     * value:api 具体
     * @see Api
     * <p>
     * 开启enableMvcUrlPrefix后,
     */

    private Map<String, Api> customApi;
    /**
     * 系统全局api访问log配置
     */
    private ApiLog apiLog = new ApiLog();
    /**
     * 系统全局api访问log配置
     */
    private Xss xss = new Xss();

    /**
     * hashId配置
     */
    private HashId hashId = new HashId();
    /**
     * 是否开启uri前缀拼接,此参数主要控制所有customApi中的api.prefix
     * 当为true时:对应包所有的api的uri将会拼上api.prefix(不建议开启,开启后idea搜索无法自动势必此前缀,建议手动在controller上加前缀)
     * 当为false时:uri将不会拼上api.prefix
     */
    private boolean enableMvcUrlPrefix = false;
    /**
     * 是否开启Cors跨域
     */
    private boolean enableCors;

    /**
     * 是否开启Servlet级异常处理器,此处理器将作为优先级最高的filter
     * 此filter拦截filter->controller(不包括)中间所有层出现的异常信息
     * 默认开启
     */
    private boolean enableServletExceptionHandle = Boolean.TRUE;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Valid
    public static class Api {

        /**
         * API 前缀，实现所有 Controller API 的统一前缀
         * <p>
         * <p>
         * 意义：通过该前缀，避免 Swagger、Actuator 意外通过 Nginx 暴露出来给外部，带来安全性问题
         * 这样，Nginx 只需要配置转发到 /api/* 的所有接口即可。
         */
        @NotEmpty(message = "API 前缀不能为空")
        private String prefix;

        /**
         * Controller 所在包的 Ant 路径规则
         * 示例:
         * 1.**.web.**
         * 2.
         * <p>
         * 主要目的是，给该 Controller 设置指定的 {@link #prefix}
         */
        @NotEmpty(message = "Controller 所在包不能为空")
        private String[] controller;

        /**
         * 是否开启uri前缀拼接
         * 当为true时:对应包所有的api的uri将会拼上api.prefix
         * 当为false时:uri将不会拼上api.prefix
         * <p>
         * 注意:此参数必须在WebConfig中的enableMvcUrlPrefix开启后才会生效
         */
        private boolean enableMvcUrlPrefix = true;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiLog {


        /**
         * 是否开启apiLong通知
         */
        private boolean enable = true;


        /**
         * 排除的uri,命中此规则的uri不会进行ApiLog通知
         * 配置此参数将覆盖默认,如希望保留系统默认,使用
         *
         * @see ApiLog#additionalExcludeUri
         * 过滤时,以excludeUri+additionalExcludeUri进行过滤
         */
        @NotNull
        private String[] excludeUri = DomainConstant.System.DEFAULT_EXCLUDE_URI;


        /**
         * 而外排除的uri,命中此规则的uri不会进行ApiLog通知
         * 和excludeUri不同,配置此参数保留系统默认的excludeUri,
         * <p>
         * 过滤时,以excludeUri+additionalExcludeUri进行过滤
         */
        @NotNull
        private String[] additionalExcludeUri = new String[]{};

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Xss {

        /**
         * 是否开启，默认为 true
         */
        private boolean enable = true;
        /**
         * 需要排除的 URL，默认为空
         */
        private List<String> excludeUrls = Collections.emptyList();

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HashId {

        /**
         * 是否开启，默认为 true
         */
        private boolean enable = true;
        /**
         * 自定义盐
         */
        private String salt = "helloWorld123";

        /**
         * 最小长度
         */
        private int minLength = 8;

    }

}
