package com.ty.mid.framework.web.core.service;

import com.ty.mid.framework.core.bus.EventPublisher;
import com.ty.mid.framework.web.core.model.ApiAccessLog;
import com.ty.mid.framework.web.core.model.ApiErrorLog;
import com.ty.mid.framework.web.core.model.event.ApiAccessLogEvent;
import com.ty.mid.framework.web.core.model.event.ApiErrorLogEvent;
import lombok.NoArgsConstructor;

/**
 * API 访问日志 Framework Service 接口 <p>
 * @author suyouliang 
 */
@NoArgsConstructor
public class ApiLogService {
    private EventPublisher eventPublisher;


    public ApiLogService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 创建 API access访问日志
     *
     * @param apiAccessLog API 访问日志
     */
    public void publishApiLog(ApiAccessLog apiAccessLog) {

        ApiAccessLogEvent apiAccessLogEvent = new ApiAccessLogEvent("api.access.log", apiAccessLog);
        eventPublisher.publish(apiAccessLogEvent);
    }

    ;

    /**
     * 创建 API error 访问日志
     *
     * @param apiErrorLog API 访问日志
     */
    public void publishApiLog(ApiErrorLog apiErrorLog) {

        ApiErrorLogEvent apiErrorLogEvent = new ApiErrorLogEvent("api.error.log", apiErrorLog);
        eventPublisher.publish(apiErrorLogEvent);
    }

    ;


}
