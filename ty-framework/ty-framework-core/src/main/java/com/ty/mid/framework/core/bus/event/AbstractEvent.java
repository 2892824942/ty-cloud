package com.ty.mid.framework.core.bus.event;

import com.ty.mid.framework.core.bus.Event;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class AbstractEvent<T extends Serializable> implements Event<T> {

    protected String topic;
    protected T source;
    protected Map<String, Object> messageProperties = new LinkedHashMap<>(0);

    public AbstractEvent(String topic, T source) {
        this.topic = topic;
        this.source = source;
    }

    public AbstractEvent(T source) {
        this.source = source;
    }

    @Override
    public String getTopic() {
        return this.topic;
    }

    @Override
    public Map<String, Object> getMessageProperties() {
        return this.messageProperties;
    }

    @Override
    public T getSource() {
        return this.source;
    }
}
