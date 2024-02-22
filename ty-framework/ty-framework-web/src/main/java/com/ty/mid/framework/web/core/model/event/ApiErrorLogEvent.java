package com.ty.mid.framework.web.core.model.event;

import com.ty.mid.framework.core.bus.event.AbstractEvent;
import com.ty.mid.framework.web.core.model.ApiErrorLog;

public class ApiErrorLogEvent extends AbstractEvent<ApiErrorLog> {
    public ApiErrorLogEvent(String topic, ApiErrorLog source) {
        super(topic, source);
    }
}
