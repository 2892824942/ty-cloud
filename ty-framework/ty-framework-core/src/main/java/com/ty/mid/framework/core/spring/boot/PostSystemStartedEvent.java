package com.ty.mid.framework.core.spring.boot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

public class PostSystemStartedEvent extends ApplicationEvent {

    private ApplicationContext ctx;

    public PostSystemStartedEvent(ApplicationContext source) {
        super(source);
        this.ctx = source;
    }

    public ApplicationContext getApplicationContext() {
        return this.ctx;
    }

}
