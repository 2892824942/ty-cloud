package com.ty.mid.framework.web;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.common.util.SafeGetUtil;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.web.swagger.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 项目启动成功后，提供文档相关的地址
 *
 * @author suyoulinag
 */
@Slf4j
public class ApplicationRunnerLauncher implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ConfigurableEnvironment env = SpringContextHelper.getEnvironment();

        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }

        Locale locale = Locale.getDefault();
        TimeZone timeZone = TimeZone.getDefault();
        Date now = new Date();

        String nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
        //可扩展配置类,可以让使用者自定义  2.支持系统级根据具体条件决定是否展示
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "{}" +
                        "Profile(s):\t\t{}\n\t" +
                        "Locale:   \t\t{}\n\t" +
                        "TimeZone: \t\t{}\n\t" +
                        "Now:     \t\t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                getConfigurableColum(protocol, env),
                env.getActiveProfiles(),
                locale,
                timeZone.getID(),
                nowStr);


    }

    private String getConfigurableColum(String protocol, ConfigurableEnvironment env) {
        return getLocal(protocol, env).concat(getExternal(protocol, env))
                .concat(getSwaggerIndex(protocol, env))
                .concat(getSwaggerDoc(protocol, env))
                .concat(getActuator(protocol, env));
    }

    private String getLocal(String protocol, ConfigurableEnvironment env) {
        return StrUtil.format("Local: \t\t\t{}://localhost:{}\n\t", protocol, env.getProperty("local.server.port"));

    }

    private String getActuator(String protocol, ConfigurableEnvironment env) {
        Boolean enable = env.getProperty("management.endpoints.beans.enabled", Boolean.class);
        if (Objects.isNull(enable) || enable) {
            return StrUtil.format("External: \t\t{}://{}:{}\n\t", protocol, env.getProperty("management.server.port")
                    , Optional.ofNullable(env.getProperty("management.endpoints.web.base-path")).orElse("/actuator"));
        }
        return StrUtil.EMPTY;
    }

    private String getExternal(String protocol, ConfigurableEnvironment env) {
        try {
            return StrUtil.format("External: \t\t{}://{}:{}\n\t", protocol, InetAddress.getLocalHost().getHostAddress(), env.getProperty("local.server.port"));
        } catch (UnknownHostException e) {
            log.warn("UnKnowHost", e);
        }
        return StrUtil.EMPTY;
    }

    private String getSwaggerIndex(String protocol, ConfigurableEnvironment env) {
        if (!isSupportSwagger(env)) {
            return StrUtil.EMPTY;
        }

        //获取swagger地址
        String swaggerIndex = "swagger-ui.html";
        return StrUtil.format("SwaggerIndex: \t\t{}/{}\n\t", getFullServiceUrl(protocol, env), swaggerIndex);
    }

    private String getSwaggerDoc(String protocol, ConfigurableEnvironment env) {
        if (!isSupportSwagger(env)) {
            return StrUtil.EMPTY;
        }

        //获取swagger地址
        String swaggerDoc = SafeGetUtil.getOrDefault(env.getProperty("springdoc.api-docs.path", String.class), "v3/api-docs");
        return StrUtil.format("SwaggerDoc: \t\t{}/{}\n\t", getFullServiceUrl(protocol, env), swaggerDoc);
    }

    private boolean isSupportSwagger(ConfigurableEnvironment env) {
        SwaggerConfig swaggerConfig = SpringContextHelper.getBean(SwaggerConfig.class);
        if (Objects.isNull(swaggerConfig)) {
            return false;
        }
        OpenAPI openAPI = SpringContextHelper.getBean(OpenAPI.class);
        if (Objects.isNull(openAPI)) {
            return false;
        }
        Boolean apiDocsEnabled = env.getProperty("springdoc.api-docs.enabled", Boolean.class);
        return Objects.isNull(apiDocsEnabled) || apiDocsEnabled;
    }

    private String getFullServiceUrl(String protocol, ConfigurableEnvironment env) {
        String contextPath = SafeGetUtil.getString(env.getProperty("server.servlet.context-path"));
        String port = SafeGetUtil.getString(env.getProperty("local.server.port"));
        try {
            return StrUtil.format("{}://{}:{}", protocol, InetAddress.getLocalHost().getHostAddress()
                    , port.concat(contextPath));
        } catch (UnknownHostException e) {
            log.warn("UnKnowHost", e);
        }
        return StrUtil.EMPTY;

    }
}

