package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.core.async.ExceptionHandlingAsyncTaskExecutor;
import com.ty.mid.framework.core.config.AsyncConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ErrorHandler;

import java.util.concurrent.Executor;

@Slf4j
@ConditionalOnBean(AsyncAnnotationBeanPostProcessor.class)
public class AsyncAutoConfiguration implements AsyncConfigurer {

    @Autowired
    private AsyncConfiguration configuration;

    protected ThreadPoolTaskExecutor executor() {
        return new ThreadPoolTaskExecutor();
    }


    @Override
    public Executor getAsyncExecutor() {
        log.debug("Creating Async Task Executor by framework!!");
        ThreadPoolTaskExecutor executor = executor();
        executor.setCorePoolSize(configuration.getCorePoolSize());
        executor.setMaxPoolSize(configuration.getMaxPoolSize());
        executor.setQueueCapacity(configuration.getQueueCapacity());
        executor.setThreadNamePrefix(configuration.getThreadNamePrefix());
        executor.initialize();
        return new ExceptionHandlingAsyncTaskExecutor(executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorHandler errorHandler() {
        return t -> log.error("error when async before  method:", t);
    }

    @Bean("asyncApplicationEventMulticaster")
    public SimpleApplicationEventMulticaster simpleApplicationEventMulticaster(ErrorHandler errorHandler) {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(getAsyncExecutor());
        multicaster.setErrorHandler(errorHandler);
        return multicaster;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

}
