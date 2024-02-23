package com.ty.mid.framework.web.core.model;

import com.ty.mid.framework.common.pojo.BaseResult;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * API 访问日志
 *
 * @author 芋道源码
 */
@Data
public class ApiLog implements Serializable {
    /**
     * 结果码
     */
    @NotNull(message = "错误码不能为空")
    private String resultCode;
    /**
     * 结果提示
     */
    private String resultMsg;
    /**
     * 链路追踪编号
     */
    private String traceId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型
     */
    private Integer userType;
    /**
     * 应用名
     */
    @NotNull(message = "应用名不能为空")
    private String applicationName;

    /**
     * 请求方法名
     */
    @NotNull(message = "http 请求方法不能为空")
    private String requestMethod;
    /**
     * 访问地址
     */
    @NotNull(message = "访问地址不能为空")
    private String requestUrl;
    /**
     * 请求参数
     */
    @NotNull(message = "请求参数不能为空")
    private Map<String, Object> requestParams;

    /**
     * 返回结果
     */
    private BaseResult<?> result;
    /**
     * 用户 IP
     */
    @NotNull(message = "ip 不能为空")
    private String userIp;
    /**
     * 浏览器 UA
     */
    @NotNull(message = "User-Agent 不能为空")
    private String userAgent;

}
