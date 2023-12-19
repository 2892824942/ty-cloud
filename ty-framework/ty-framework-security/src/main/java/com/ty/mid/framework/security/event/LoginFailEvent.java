package com.ty.mid.framework.security.event;

import com.ty.mid.framework.core.bus.event.AbstractEvent;
import com.ty.mid.framework.security.AuthToken;
import lombok.Getter;

@Getter
public class LoginFailEvent<T extends AuthToken> extends AbstractEvent<T> {

    private Exception error;

    public LoginFailEvent(String topic, T source) {
        super(topic, source);
    }

    public LoginFailEvent(T source) {
        super(source);
    }

    public LoginFailEvent(String topic, T source, Exception error) {
        super(topic, source);
        this.error = error;
    }

    public LoginFailEvent(T source, Exception error) {
        super(source);
        this.error = error;
    }
}
