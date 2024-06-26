package com.ty.mid.framework.web.core.filter;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ty.mid.framework.common.exception.enums.GlobalErrorCodeEnum;
import com.ty.mid.framework.common.pojo.BaseResult;
import com.ty.mid.framework.common.util.JsonUtils;
import com.ty.mid.framework.core.monitor.TracerUtils;
import com.ty.mid.framework.core.util.servlet.ServletUtils;
import com.ty.mid.framework.web.config.WebConfig;
import com.ty.mid.framework.web.core.model.ApiAccessLog;
import com.ty.mid.framework.web.core.service.ApiLogService;
import com.ty.mid.framework.web.core.util.WebFrameworkUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;


/**
 * API 访问日志 Filter <p>
 * @author suyouliang 
 */
@Slf4j
public class ApiAccessLogFilter extends ApiRequestFilter {

    private final String applicationName;

    @Resource
    private ApiLogService apiLogService;

    public ApiAccessLogFilter(WebConfig webConfig, ApiLogService apiLogService, String applicationName) {
        super(webConfig);
        this.apiLogService = apiLogService;
        this.applicationName = applicationName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 获得开始时间
        LocalDateTime beginTime = LocalDateTime.now();
        // 提前获得参数，避免 XssFilter 过滤处理
        Map<String, String> queryString = ServletUtils.getParamMap(request);
        //解析出的requestBody中有很多\r\n以及空字符,这里处理
        Map<String, Object> bodyMap = StrUtil.isNotBlank(ServletUtils.getBody(request))
                ? JsonUtils.parseObject(ServletUtils.getBody(request), new TypeReference<Map<String, Object>>() {})
                : Collections.emptyMap();

        try {
            // 继续过滤器
            filterChain.doFilter(request, response);
            // 正常执行，记录日志
            createApiAccessLog(request, beginTime, queryString, bodyMap, null);
        } catch (Exception ex) {
            // 异常执行，记录日志
            createApiAccessLog(request, beginTime, queryString, bodyMap, ex);
            throw ex;
        }
    }

    private void createApiAccessLog(HttpServletRequest request, LocalDateTime beginTime,
                                    Map<String, String> queryString, Map<String, Object> requestBody, Exception ex) {
        ApiAccessLog accessLog = new ApiAccessLog();
        try {
            this.buildApiAccessLogDTO(accessLog, request, beginTime, queryString, requestBody, ex);
            apiLogService.publishApiLog(accessLog);
        } catch (Throwable th) {
            log.error("[createApiAccessLog][url({}) log({}) 发生异常]", request.getRequestURI(), JsonUtils.toJson(accessLog), th);
        }
    }

    private void buildApiAccessLogDTO(ApiAccessLog accessLog, HttpServletRequest request, LocalDateTime beginTime,
                                      Map<String, String> queryString, Map<String, Object> requestBody, Exception ex) {
        // 处理用户信息
        accessLog.setUserId(WebFrameworkUtils.getLoginUserId(request));
        accessLog.setUserType(WebFrameworkUtils.getLoginUserType(request));
        // 设置访问结果
        BaseResult<?> result = WebFrameworkUtils.getCommonResult(request);
        if (result != null) {
            accessLog.setResultCode(result.getCode());
            accessLog.setResultMsg(result.getMessage());
        } else if (ex != null) {
            accessLog.setResultCode(GlobalErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode());
            accessLog.setResultMsg(ExceptionUtil.getRootCauseMessage(ex));
        } else {
            accessLog.setResultCode(GlobalErrorCodeEnum.SUCCESS.getCode());
            accessLog.setResultMsg("");
        }
        accessLog.setResult(result);
        // 设置其它字段
        accessLog.setTraceId(TracerUtils.getTraceId());
        accessLog.setApplicationName(applicationName);
        accessLog.setRequestUrl(request.getRequestURI());

        Map<String, Object> requestParams = MapUtil.<String, Object>builder().put("query", queryString).put("body", requestBody).build();
        accessLog.setRequestParams(requestParams);
        accessLog.setRequestMethod(request.getMethod());
        accessLog.setUserAgent(ServletUtils.getUserAgent(request));
        accessLog.setUserIp(ServletUtils.getClientIP(request));
        // 持续时间
        accessLog.setBeginTime(beginTime);
        accessLog.setEndTime(LocalDateTime.now());
        accessLog.setDuration((int) LocalDateTimeUtil.between(accessLog.getBeginTime(), accessLog.getEndTime(), ChronoUnit.MILLIS));
    }

}
