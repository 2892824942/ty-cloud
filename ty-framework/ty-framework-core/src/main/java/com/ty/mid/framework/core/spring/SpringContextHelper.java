package com.ty.mid.framework.core.spring;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.ty.mid.framework.common.lang.NonNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.AbstractApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class SpringContextHelper implements ApplicationContextAware {

    @Getter
    private static ApplicationContext context;
    private static ConfigurableEnvironment env;

    /**
     * 根据类型获取bean,如果bean不存在会报错
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(@NonNull Class<T> clazz) {
        return context.getBean(clazz);
    }


    /**
     * 根据类型获取bean,如果bean不存在不会报错,但会返回null
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBeanSafety(@NonNull Class<T> clazz) {
        try {
            return context.getBean(clazz);
        } catch (BeansException e) {
            //ignore
            return null;
        }
    }

    /**
     * 获取系统定义的,支持某个特定事件的ApplicationListener集合
     * 此方法支持Spring的ApplicationEvent以及自定义的Event
     *
     * @param eventClass event class
     * @return List<ApplicationListener < ?>>
     */
    public static List<ApplicationListener<?>> getApplicationListener(Class<?> eventClass) {
        Map<String, AbstractApplicationEventMulticaster> beansOfTypeMap = SpringContextHelper.getBeansOfType(AbstractApplicationEventMulticaster.class);
        if (CollUtil.isEmpty(beansOfTypeMap)) {
            return null;
        }
        return beansOfTypeMap.values().stream().map(abstractApplicationEventMulticaster -> {
            Method getApplicationListenersMethod = ReflectUtil.getMethod(AbstractApplicationEventMulticaster.class, "getApplicationListeners");
            Collection<ApplicationListener<?>> applicationListeners = ReflectUtil.invoke(abstractApplicationEventMulticaster, getApplicationListenersMethod);
            if (CollUtil.isEmpty(applicationListeners)) {
                return null;
            }
            return applicationListeners.stream().filter(applicationListener -> {
                Method supportsEventMethod = ReflectUtil.getMethod(AbstractApplicationEventMulticaster.class, "supportsEvent", ApplicationListener.class, ResolvableType.class, Class.class);
                return ReflectUtil.invoke(abstractApplicationEventMulticaster, supportsEventMethod, applicationListener, ResolvableType.forClass(eventClass), eventClass);
            }).filter(Objects::nonNull).collect(Collectors.toList());

        }).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * 是否存在bean
     *
     * @param name bean的名称
     * @return
     */
    public static boolean containsBean(@NonNull String name) {
        return context.containsBean(name);
    }

    public static <T> T getBean(@NonNull String name, @NonNull Class<T> requireType) {
        return context.getBean(name, requireType);
    }

    /**
     * 据类型获取bean,如果bean不存在不会报错,但会返回null
     *
     * @param name        bean名称
     * @param requireType 类型
     */
    public static <T> T getBeanSafety(@NonNull String name, @NonNull Class<T> requireType) {
        try {
            return context.getBean(name, requireType);
        } catch (BeansException e) {
            //ignore
            return null;
        }
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    /**
     * 根据注解获取 bean
     *
     * @param annotationClass
     * @return
     */
    public static Map<String, Object> getBeansWithAnnotation(@NonNull Class<? extends Annotation> annotationClass) {
        return context.getBeansWithAnnotation(annotationClass);
    }

    public static Resource getResource(String location) {
        return context.getResource(location);
    }

    public static Resource[] getResources(String locationPattern) {
        Resource[] ret = null;
        try {
            ret = context.getResources(locationPattern);
        } catch (IOException e) {
            log.warn("get resources error, source pattern: {}", locationPattern, e);
        }
        return ret;
    }

    public static ConfigurableEnvironment getEnvironment() {
        return env;
    }

    public static String getApplicationName() {
        return context.getApplicationName();
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        return env.getProperty(key, targetType);
    }

    public static String getProperty(String key) {
        return env.getProperty(key, String.class);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return env.getProperty(key, targetType, defaultValue);
    }

    public static String getProperty(String key, String defaultValue) {
        return env.getProperty(key, defaultValue);
    }

    public static <T> T getRequiredProperty(String key, Class<T> targetType) {
        return env.getRequiredProperty(key, targetType);
    }

    public static String getRequiredProperty(String key) {
        return env.getRequiredProperty(key);
    }

    public static ConversionService getConversionService() {
        return env.getConversionService();
    }

    public static <T> T convert(Object source, Class<T> targetType) {
        return getConversionService().convert(source, targetType);
    }

    public static boolean containsProfile(String profile) {
        return env.acceptsProfiles(profile);
    }

    public static String[] getActiveProfiles() {
        return env.getActiveProfiles();
    }

    public static void setActiveProfiles(String[] profiles) {
        env.setActiveProfiles(profiles);
    }

    public static String getActiveProfile() {
        return env.getActiveProfiles()[0];
    }

    public static void addActiveProfile(String profile) {
        env.addActiveProfile(profile);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        env = (ConfigurableEnvironment) context.getEnvironment();
    }

}