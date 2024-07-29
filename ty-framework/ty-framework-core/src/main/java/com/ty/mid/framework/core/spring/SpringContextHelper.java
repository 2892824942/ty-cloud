package com.ty.mid.framework.core.spring;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.ty.mid.framework.common.constant.EnvConstant;
import com.ty.mid.framework.common.lang.NonNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
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

/**
 * 使用说明:
 * 此类推荐在SpringContext prepared阶段后使用
 * <p>
 * 如需在prepared之前使用<p>
 * 可能存在对应上下文未初始化导致报错的情况.此时需要按照SpringBoot生命周期上下文加载顺序
 * ,调整对应代码,比如使用Env,可在EnvironmentPreparedEvent事件时进行对应的逻辑操作,以达到对应上下文prepared的第一时间执行
 */
@Slf4j
public class SpringContextHelper implements ApplicationContextAware {

    @Getter
    private static ConfigurableApplicationContext context;
    private static ConfigurableEnvironment env;

    private static ConfigurableListableBeanFactory beanFactory;


    /******************************************Bean操作相关******************************************/

    public static <T> T getBean(@NonNull String name, @NonNull Class<T> requireType) {
        return context.getBean(name, requireType);
    }

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
     * 是否存在bean
     *
     * @param name bean的名称
     * @return
     */
    public static boolean containsBean(@NonNull String name) {
        return context.containsBean(name);
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

    /**
     * 添加单例bean,适合简单场景动态添加bean
     * 注意:
     * 此类方式添加的bean,不会执行任何初始化回调（特别是，它不会调用 InitializingBean 的 afterPropertiesSet 方法）。
     * 且给定的实例也不会收到任何回收回调（如 DisposableBean 的 destroy 方法）
     *
     * @return
     */
    public static void registerSingleton(Object singletonObject) {
        beanFactory.registerSingleton(singletonObject.getClass().getSimpleName(), singletonObject);
    }

    /**
     * 创建bean,适合复杂场景动态添加bean
     * 执行 bean 的完整初始化，包括所有适用的 BeanPostProcessors以及回收回调
     *
     * @return
     */
    public static <T> T createBean(Class<T> beanClass) {
        return beanFactory.createBean(beanClass);
    }

    /**
     * 创建bean,适合复杂场景动态添加bean,可控制生命周期细粒度操作
     * 执行 bean 的完整初始化，包括所有适用的 BeanPostProcessors以及回收回调
     *
     * @return
     */
    public static Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
        return beanFactory.createBean(beanClass, autowireMode, dependencyCheck);
    }

    /******************************************资源相关******************************************/
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

    /******************************************配置相关******************************************/

    public static ConfigurableEnvironment getEnvironment() {
        return env;
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

    /******************************************环境相关******************************************/

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

    /**
     * 是否包含某个环境(忽略大小写)
     *
     * @return boolean
     */
    public static boolean containsProfile(String profile) {
        return env.matchesProfiles(profile);
    }


    /**
     * 是否是dev环境(忽略大小写)
     *
     * @return boolean
     */
    public static boolean isDev() {
        return containsProfile(EnvConstant.DEV);
    }

    /**
     * 是否是测试环境(忽略大小写)
     *
     * @return boolean
     */
    public static boolean isTest() {
        return containsProfile(EnvConstant.TEST);
    }


    /**
     * 是否是灰度或生产环境(忽略大小写)
     *
     * @return boolean
     */
    public static boolean isPreOrProd() {
        return containsProfile(EnvConstant.PRE) || containsProfile(EnvConstant.PROD);
    }

    /**
     * 是否不是灰度或生产环境(忽略大小写)
     *
     * @return boolean
     */
    public static boolean isNotPreOrProd() {
        return !containsProfile(EnvConstant.PRE) && !containsProfile(EnvConstant.PROD);
    }


    /******************************************其他******************************************/
    public static String getApplicationName() {
        return context.getApplicationName();
    }

    public static ConversionService getConversionService() {
        return env.getConversionService();
    }

    public static <T> T convert(Object source, Class<T> targetType) {
        return getConversionService().convert(source, targetType);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = (ConfigurableApplicationContext) applicationContext;
        env = context.getEnvironment();
    }

}