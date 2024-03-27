package com.ty.mid.framework.service.config;

import com.ty.mid.framework.service.wrapper.UserNameTranslation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@Slf4j
public class ServiceAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    UserNameTranslation userNameTranslation() {
        return userIdList -> Collections.emptyMap();
    }
}
