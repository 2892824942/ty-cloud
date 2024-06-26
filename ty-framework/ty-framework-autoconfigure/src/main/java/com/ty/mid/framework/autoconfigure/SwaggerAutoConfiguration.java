package com.ty.mid.framework.autoconfigure;

import cn.dev33.satoken.config.SaTokenConfig;
import com.ty.mid.framework.web.config.WebConfig;
import com.ty.mid.framework.web.swagger.SwaggerUtil;
import com.ty.mid.framework.web.swagger.config.SwaggerConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.*;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.providers.JavadocProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import java.util.*;

/**
 * Swagger 自动配置类，基于 OpenAPI + Springdoc 实现。 <p>
 * 友情提示： <p>
 * 1. Springdoc 文档地址：<a href="https://github.com/springdoc/springdoc-openapi">仓库</a> <p>
 * 2. Swagger 规范，于 2015 更名为 OpenAPI 规范，本质是一个东西 <p>
 *
 * @author suyouliang
 */
@ConditionalOnClass({OpenAPI.class})
@EnableConfigurationProperties({SwaggerConfig.class, WebConfig.class})
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoConfiguration {

    // ========== 全局 OpenAPI 配置 ==========
    @Autowired
    private ObjectProvider<SaTokenConfig> SaTokenConfigProvider;

    @Bean
    public OpenAPI createApi(SwaggerConfig properties) {
        Map<String, SecurityScheme> securitySchemas = buildSecuritySchemes();
        OpenAPI openAPI = new OpenAPI()
                // 接口信息
                .info(buildInfo(properties))
                // 接口安全配置
                .components(new Components().securitySchemes(securitySchemas))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
        securitySchemas.keySet().forEach(key -> openAPI.addSecurityItem(new SecurityRequirement().addList(key)));
        return openAPI;
    }

    /**
     * API 摘要信息
     */
    private Info buildInfo(SwaggerConfig properties) {
        return new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .contact(new Contact().name(properties.getAuthor()).url(properties.getUrl()).email(properties.getEmail()))
                .license(new License().name(properties.getLicense()).url(properties.getLicenseUrl()));
    }

    /**
     * 安全模式，这里配置通过请求头 Authorization 传递 token 参数
     */
    private Map<String, SecurityScheme> buildSecuritySchemes() {
        Map<String, SecurityScheme> securitySchemes = new HashMap<>();
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY) // 类型
                .name(HttpHeaders.AUTHORIZATION) // 请求头的 name
                .in(SecurityScheme.In.HEADER); // token 所在位置
        securitySchemes.put(HttpHeaders.AUTHORIZATION, securityScheme);
        return securitySchemes;
    }

    /**
     * 自定义 OpenAPI 处理器
     */
    @Bean
    public OpenAPIService openApiBuilder(Optional<OpenAPI> openAPI,
                                         SecurityService securityParser,
                                         SpringDocConfigProperties springDocConfigProperties,
                                         PropertyResolverUtils propertyResolverUtils,
                                         Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers,
                                         Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers,
                                         Optional<JavadocProvider> javadocProvider) {

        return new OpenAPIService(openAPI, securityParser, springDocConfigProperties,
                propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
    }

    // ========== 分组 OpenAPI 配置 ==========

    /**
     * 所有模块的 API 分组
     */
    @Bean
    public GroupedOpenApi allGroupedOpenApi() {
        GroupedOpenApi groupedOpenApi = SwaggerUtil.buildGroupedOpenApi("all", "");
        return handlerApiSecurityParam(groupedOpenApi);
    }

    public GroupedOpenApi handlerApiSecurityParam(GroupedOpenApi groupedOpenApi) {
        SaTokenConfig saTokenConfig = SaTokenConfigProvider.getIfAvailable();
        if (Objects.isNull(saTokenConfig)) {
            return groupedOpenApi;
        }
        ArrayList<OperationCustomizer> operationCustomizer = new ArrayList<>();
        operationCustomizer.add((operation, handlerMethod) -> {
            if (saTokenConfig.getIsReadHeader()) {
                operation.addParametersItem(SwaggerUtil.buildSecurityHeaderParameter(saTokenConfig.getTokenName(), SecurityScheme.In.HEADER));
            }
            if (saTokenConfig.getIsReadCookie()) {
                operation.addParametersItem(SwaggerUtil.buildSecurityHeaderParameter(saTokenConfig.getTokenName(), SecurityScheme.In.COOKIE));
            } else if (saTokenConfig.getIsReadBody()) {
                //同时添加cookie及query,会在界面上显示两个,这里使用排他方式
                operation.addParametersItem(SwaggerUtil.buildSecurityHeaderParameter(saTokenConfig.getTokenName(), SecurityScheme.In.QUERY));
            }
            return operation;
        });
        groupedOpenApi.addAllOperationCustomizer(operationCustomizer);
        return groupedOpenApi;
    }


}

