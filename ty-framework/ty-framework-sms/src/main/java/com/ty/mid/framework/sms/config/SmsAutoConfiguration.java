package com.ty.mid.framework.sms.config;

import com.ty.mid.framework.sms.local.LocalFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;


@Slf4j
public class SmsAutoConfiguration {

    @Bean
    public LocalFactory localFactory() {
        return LocalFactory.instance();
    }
}
