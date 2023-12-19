package com.ty.mid.framework.security.event;

import com.ty.mid.framework.core.bus.event.AbstractEvent;
import com.ty.mid.framework.security.AuthToken;
import lombok.Getter;

@Getter
public class PostLoginEvent<T extends AuthToken> extends AbstractEvent<T> {
    public PostLoginEvent(String topic, T source) {
        super(topic, source);
    }

    public PostLoginEvent(T source) {
        super(source);
    }
}
