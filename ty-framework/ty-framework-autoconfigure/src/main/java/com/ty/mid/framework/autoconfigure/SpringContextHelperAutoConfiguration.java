package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.core.spring.SpringContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@Slf4j
public class SpringContextHelperAutoConfiguration {

    private final ConfigurableApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    SpringContextHelper springContextHelper() {
        SpringContextHelper helper = new SpringContextHelper();
        helper.setApplicationContext(applicationContext);
        log.info("[init] SpringContextHelper");
        return helper;
    }

}
