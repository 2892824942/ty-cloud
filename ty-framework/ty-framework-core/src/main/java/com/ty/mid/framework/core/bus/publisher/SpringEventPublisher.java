package com.ty.mid.framework.core.bus.publisher;

import cn.hutool.core.lang.Assert;
import com.ty.mid.framework.core.bus.Event;
import com.ty.mid.framework.core.bus.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;

public class SpringEventPublisher implements EventPublisher {

    private ApplicationEventPublisher publisher;

    public SpringEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public <T extends Serializable> void publish(Event<T> event) {
        Assert.notNull(event, "the event to publish can not be null!");
        this.publisher.publishEvent(event);

    }
}
