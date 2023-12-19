package com.ty.mid.framework.security.event;

import com.ty.mid.framework.core.bus.event.AbstractEvent;
import com.ty.mid.framework.security.AuthToken;
import lombok.Getter;

@Getter
public class PreLoginEvent<T extends AuthToken> extends AbstractEvent<T> {
    public PreLoginEvent(String topic, T source) {
        super(topic, source);
    }

    public PreLoginEvent(T source) {
        super(source);
    }
}
