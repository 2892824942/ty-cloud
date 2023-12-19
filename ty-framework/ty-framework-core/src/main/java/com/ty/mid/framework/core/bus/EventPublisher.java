package com.ty.mid.framework.core.bus;

import java.io.Serializable;

public interface EventPublisher {

    /**
     * 发布事件
     *
     * @param event
     * @param <T>
     */
    <T extends Serializable> void publish(Event<T> event);

}
