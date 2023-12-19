package com.ty.mid.framework.core.bus;

import java.io.Serializable;
import java.util.Map;

public interface Event<T extends Serializable> {

    /**
     * topic
     *
     * @return
     */
    String getTopic();

    /**
     * message properties
     * you know, for kafka、rabbitMQ
     *
     * @return
     */
    Map<String, Object> getMessageProperties();

    /**
     * the event source
     *
     * @return
     */
    T getSource();

}
