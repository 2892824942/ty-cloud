package com.ty.mid.framework.core.spring;

import com.ty.mid.framework.common.lang.NonNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;

@Slf4j
public class SpringContextHelper implements ApplicationContextAware {

    @Getter
    private static ApplicationContext context;
    private static ConfigurableEnvironment env;

    /**
     * 根据类型获取bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(@NonNull Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(@NonNull String name, @NonNull Class<T> requireType) {
        return context.getBean(name, requireType);
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