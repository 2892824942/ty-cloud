package com.ty.mid.framework.security.event;

import com.ty.mid.framework.common.model.UserLogin;
import com.ty.mid.framework.core.bus.event.AbstractEvent;
import com.ty.mid.framework.security.AuthenticationResult;
import lombok.Getter;

@Getter
public class LoginSuccessEvent<T extends AuthenticationResult> extends AbstractEvent<T> {

    private UserLogin<?> userLogin;

    public <ID> LoginSuccessEvent(String topic, T source, UserLogin<ID> userLogin) {
        super(topic, source);
        this.userLogin = userLogin;
    }

    public <ID> LoginSuccessEvent(T source, UserLogin<ID> userLogin) {
        super(source);
        this.userLogin = userLogin;
    }
}
