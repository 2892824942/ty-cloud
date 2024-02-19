package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.core.bus.EventPublisher;
import com.ty.mid.framework.core.bus.publisher.SpringEventPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class EventBusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    EventPublisher eventPublisher(ApplicationEventPublisher publisher) {
        return new SpringEventPublisher(publisher);
    }

}
