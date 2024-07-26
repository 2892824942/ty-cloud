package com.ty.mid.framework.autoconfigure;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
/**
 * 环境变量准备完成时执行,由于执行较为靠前,此事件需要手动注册,方式如下
 * org.springframework.context.ApplicationListener=\
 * com.ty.mid.framework.autoconfigure.ApplicationEnvironmentPreparedListener
 *
 */
public class ApplicationEnvironmentPreparedListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {


    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
    }
}
