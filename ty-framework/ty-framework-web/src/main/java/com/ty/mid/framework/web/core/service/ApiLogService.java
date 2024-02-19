package com.ty.mid.framework.web.core.service;

import com.ty.mid.framework.core.bus.publisher.SpringEventPublisher;
import com.ty.mid.framework.web.core.model.ApiAccessLog;
import com.ty.mid.framework.web.core.model.ApiErrorLog;
import com.ty.mid.framework.web.core.model.event.ApiAccessLogEvent;
import com.ty.mid.framework.web.core.model.event.ApiErrorLogEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * API 访问日志 Framework Service 接口
 *
 * @author 芋道源码
 */
@Component
public class ApiLogService {
    @Resource
    private SpringEventPublisher springEventPublisher;

    /**
     * 创建 API access访问日志
     *
     * @param apiAccessLog API 访问日志
     */
    public void publishApiLog(ApiAccessLog apiAccessLog) {

        ApiAccessLogEvent apiAccessLogEvent = new ApiAccessLogEvent("api.access.log", apiAccessLog);
        springEventPublisher.publish(apiAccessLogEvent);
    }

    ;

    /**
     * 创建 API error 访问日志
     *
     * @param apiErrorLog API 访问日志
     */
    public void publishApiLog(ApiErrorLog apiErrorLog) {

        ApiErrorLogEvent apiErrorLogEvent = new ApiErrorLogEvent("api.error.log", apiErrorLog);
        springEventPublisher.publish(apiErrorLogEvent);
    }

    ;


}
