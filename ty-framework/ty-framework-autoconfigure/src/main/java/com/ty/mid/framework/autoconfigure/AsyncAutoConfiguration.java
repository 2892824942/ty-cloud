package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.core.config.AsyncConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncExecutionAspectSupport;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.AsyncConfigurationSelector;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ErrorHandler;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * 执行时机:当上层项目开启@EnableAsync注解后执行
 * 自动装配内容
 * 1.支持异常自动打印日志(Future形式需要get时开发者自己捕获异常处理,目前没有找到合理方式使此情况异常日志可以自动打印)
 * 主要是spring的异常组件在线程执行异常时,Future形式没有走AsyncUncaughtExceptionHandler,而是直接泡个Futrue内部,暂时没有找到合适的方式
 * 去干预这个逻辑分支的处理,具体:
 *
 * @see AsyncExecutionAspectSupport#handleError(Throwable, Method, Object...)
 * 2支持异步组件线程池可配置
 *
 * 后续:
 * 1.动态线程池技术
 * 2.可以分组配置线程池
 */
@Slf4j
@AutoConfigureAfter(AsyncConfigurationSelector.class)
@ConditionalOnBean(AsyncAnnotationBeanPostProcessor.class)
@EnableConfigurationProperties(AsyncConfig.class)
public class AsyncAutoConfiguration implements AsyncConfigurer {

    @Autowired
    private AsyncConfig configuration;

    protected ThreadPoolTaskExecutor executor() {
        return new ThreadPoolTaskExecutor();
    }


    @Override
    public Executor getAsyncExecutor() {
        log.debug("creating async task executor by framework!!");
        ThreadPoolTaskExecutor executor = executor();
        executor.setCorePoolSize(configuration.getCorePoolSize());
        executor.setMaxPoolSize(configuration.getMaxPoolSize());
        executor.setQueueCapacity(configuration.getQueueCapacity());
        executor.setThreadNamePrefix(configuration.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorHandler errorHandler() {
        return t -> log.error("error occurred when before invoking async  method:", t);
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
        return new MyAsyncUncaughtExceptionHandler();
    }


    public static class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

        public MyAsyncUncaughtExceptionHandler() {
        }

        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            log.error("unexpected exception occurred invoking async method: ", ex);
        }
    }


}
