package com.ty.mid.framework.core.bus.event;

import java.io.Serializable;

public class SimpleEvent extends AbstractEvent {

    public SimpleEvent(String topic, Serializable source) {
        super(topic, source);
    }

    public SimpleEvent(Serializable source) {
        super(source);
    }

}
