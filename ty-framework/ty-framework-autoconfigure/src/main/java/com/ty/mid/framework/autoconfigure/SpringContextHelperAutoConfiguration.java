package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.core.spring.SpringContextHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class SpringContextHelperAutoConfiguration {

    @Resource
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    SpringContextHelper springContextHelper() {
        SpringContextHelper helper = new SpringContextHelper();
        helper.setApplicationContext(applicationContext);
        return helper;
    }

}
