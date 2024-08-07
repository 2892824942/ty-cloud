package com.ty.mid.framework.web.core.listener;

import com.ty.mid.framework.common.util.JsonUtils;
import com.ty.mid.framework.web.core.model.event.ApiErrorLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
public class DefaultApiErrorLogListener {

    public void onApiErrorLogEvent(ApiErrorLogEvent event) {
        log.error("api error log eventData: \r\n{}", JsonUtils.toJson(event.getSource()));
    }
}
