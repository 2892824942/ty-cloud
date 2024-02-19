package com.ty.mid.framework.web.core.model.event;

import com.ty.mid.framework.core.bus.event.AbstractEvent;
import com.ty.mid.framework.web.core.model.ApiAccessLog;

public class ApiAccessLogEvent extends AbstractEvent<ApiAccessLog> {
    public ApiAccessLogEvent(String topic, ApiAccessLog source) {
        super(topic, source);
    }
}
