package com.ty.mid.framework.web.core.listener;

import com.ty.mid.framework.common.util.JsonUtils;
import com.ty.mid.framework.web.core.model.event.ApiAccessLogEvent;
import com.ty.mid.framework.web.core.model.event.ApiErrorLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

@Slf4j
public class DefaultApiAccessLogListener {

    public void onApiAccessLogEvent(ApiAccessLogEvent event) {
        log.info("api access log eventData: \r\n{}", JsonUtils.toJson(event.getSource()));
    }
}
