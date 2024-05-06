package com.ty.mid.framework.web.swagger;

import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;

public class SwaggerUtil {
    /**
     * 构建分组的api
     *
     * @param group
     * @param path
     * @return
     */
    public static GroupedOpenApi buildGroupedOpenApi(String group, String path) {
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch(path + "/**")
                .build();
    }

    /**
     * 构建 Tenant 租户编号请求头参数
     *
     * @return 多租户参数
     */
    public static Parameter buildTenantHeaderParameter(String tenantName, SecurityScheme.In in) {
        return new Parameter()
                .name(tenantName) // header 名
                .description("租户编号") // 描述
                .in(String.valueOf(in)) // 请求 header
                .schema(new IntegerSchema()._default(1L).name(tenantName).description("租户编号")); // 默认：使用租户编号为 1
    }

    /**
     * 构建 Authorization 认证请求头参数
     * <p>
     * 解决 Knife4j <a href="https://gitee.com/xiaoym/knife4j/issues/I69QBU">Authorize 未生效，请求header里未包含参数</a>
     *
     * @return 认证参数
     */
    public static Parameter buildSecurityHeaderParameter(String authorizationName, SecurityScheme.In in) {
        return new Parameter()
                .name(authorizationName) // header 名
                .description("认证authorization") // 描述
                .in(String.valueOf(in)) // 请求 header
                .schema(new StringSchema()._default("Bearer test").name("token").description("authorization"));
    }
}
