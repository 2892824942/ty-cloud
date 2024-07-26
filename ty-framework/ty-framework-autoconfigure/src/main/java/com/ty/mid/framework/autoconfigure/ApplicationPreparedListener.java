package com.ty.mid.framework.autoconfigure;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.web.core.listener.DefaultApiErrorLogListener;
import com.ty.mid.framework.web.core.listener.DefaultApiLogListener;
import com.ty.mid.framework.web.core.model.event.ApiAccessLogEvent;
import com.ty.mid.framework.web.core.model.event.ApiErrorLogEvent;
import com.ty.mid.framework.web.core.service.ApiLogService;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.context.event.DefaultEventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * spring环境准备完成时执行
 */
public class ApplicationPreparedListener implements ApplicationListener<ApplicationPreparedEvent> {


    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        configDefaultApiLogListener(applicationContext);

    }

    /**
     * <p>web模块默认日志监听器加载,如果用户定义了对应的监听器,则默认监听器不会加载
     * <p>说明:
     * <p>由于ConfigurableApplicationContext只提供了SpringEvent事件监听器的添加api,没有暴露自定义的Event监听器的添加api,
     * <p>这里使用反射模拟自定义的Event监听器初始化过程,动态添加
     *
     *
     * @see EventListenerMethodProcessor#processBean(String, Class)
     * <p>
     * 关于监听器加载问题:
     * <p>
     * 现在默认加载有几种方式:
     * <p>1.框架使用DefaultLogListener处理,并定义处理handler接口,用户自己实现handler接口,可自定义log日志处理逻辑,这个过程DefaultLogListener必加载,用户需要知道具体的handler细节
     * <p>2.框架使用DefaultLogListener处理,并根据用户是否对相应的事件定义了监听器,来决定是否加载DefaultLogListener,
     * <p>用户只需要知道,定义了对应事件的监听器,默认监听就会失效.而定义监听器是我们熟知的spring监听器定义操作,这个无需额外深入了解本框架的加载细节(目前使用的方式)
     * <p>
     * <p>对于2,如果不苛求DefaultLogListener加载成本,还可以使用其他方式来避免以下复杂且过多反射逻辑:
     * <p>1.默认日志处理增加开关控制暴露给用户,DefaultLogListener始终会加载,关闭开关后仅处理逻辑不执行
     * <p>2.先正常加载DefaultLogListener,在ApplicationContext准备完成时,检查相关Event是否存在其他定义的监听器,如果有,删除默认的监听器(通过AbstractApplicationEventMulticaster)
     * //TODO 考虑到大量反射对于版本过度依赖,更换版本很容易不兼容,后续根据情况考虑是否使用2方式,先加在删,api受版本更改影响较少
     * @param applicationContext applicationContext
     */
    private void configDefaultApiLogListener(ConfigurableApplicationContext applicationContext) {
        ApiLogService apiLogService = SpringContextHelper.getBeanSafety(ApiLogService.class);

        if (Objects.isNull(apiLogService)) {
            //没有依赖web模块,直接跳过
            return;
        }

        List<ApplicationListener<?>> apiAccessLogApplicationListener = SpringContextHelper.getApplicationListener(ApiAccessLogEvent.class);
        List<ApplicationListener<?>> apiErrorLogApplicationListener = SpringContextHelper.getApplicationListener(ApiErrorLogEvent.class);
        if (CollUtil.isNotEmpty(apiAccessLogApplicationListener) && CollUtil.isNotEmpty(apiErrorLogApplicationListener)) {
            //已经自定义对应的监听器,跳过默认监听器装载
            return;
        }

        DefaultEventListenerFactory listenerFactory = SpringContextHelper.getBean(DefaultEventListenerFactory.class);
        EventListenerMethodProcessor eventListenerMethodProcessor = SpringContextHelper.getBean(EventListenerMethodProcessor.class);
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        if (CollUtil.isEmpty(apiAccessLogApplicationListener)) {
            beanFactory.registerSingleton(DefaultApiLogListener.class.getSimpleName(), new DefaultApiLogListener());
            Method onApiAccessLogEventMethod = ReflectUtil.getMethod(DefaultApiLogListener.class, "onApiAccessLogEvent", ApiAccessLogEvent.class);
            ApplicationListener<?> defaultApiAccessLogListener = listenerFactory.createApplicationListener(DefaultApiLogListener.class.getSimpleName(), ApiAccessLogEvent.class, onApiAccessLogEventMethod);
            //动态注册默认的监听处理器
            ApplicationListenerMethodAdapter applicationListenerMethodAdapter = (ApplicationListenerMethodAdapter) defaultApiAccessLogListener;

            ReflectUtil.invoke(applicationListenerMethodAdapter, "init", applicationContext, ReflectUtil.getFieldValue(eventListenerMethodProcessor, "evaluator"));
            applicationContext.addApplicationListener(defaultApiAccessLogListener);
        }

        if (CollUtil.isEmpty(apiErrorLogApplicationListener)) {
            beanFactory.registerSingleton(DefaultApiErrorLogListener.class.getSimpleName(), new DefaultApiErrorLogListener());
            Method onApiErrorLogEventMethod = ReflectUtil.getMethod(DefaultApiErrorLogListener.class, "onApiErrorLogEvent", ApiErrorLogEvent.class);
            ApplicationListener<?> defaultApiErrorLogListener = listenerFactory.createApplicationListener(DefaultApiErrorLogListener.class.getSimpleName(), ApiErrorLogEvent.class, onApiErrorLogEventMethod);
            //动态注册默认的监听处理器
            ApplicationListenerMethodAdapter applicationListenerMethodAdapter = (ApplicationListenerMethodAdapter) defaultApiErrorLogListener;
            ReflectUtil.invoke(applicationListenerMethodAdapter, "init", applicationContext, ReflectUtil.getFieldValue(eventListenerMethodProcessor, "evaluator"));
            applicationContext.addApplicationListener(defaultApiErrorLogListener);
        }
    }


}
