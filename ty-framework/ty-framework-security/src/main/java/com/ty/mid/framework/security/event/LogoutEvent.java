package com.ty.mid.framework.security.event;

import com.ty.mid.framework.common.model.UserLogin;
import com.ty.mid.framework.core.bus.event.AbstractEvent;
import lombok.Getter;

@Getter
public class LogoutEvent<T extends UserLogin> extends AbstractEvent<T> {

    public LogoutEvent(String topic, T source) {
        super(topic, source);
    }

    public LogoutEvent(T source) {
        super(source);
    }
}
