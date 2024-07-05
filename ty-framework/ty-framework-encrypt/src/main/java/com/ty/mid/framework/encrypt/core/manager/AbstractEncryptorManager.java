package com.ty.mid.framework.encrypt.core.manager;

import cn.hutool.core.lang.Assert;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.encrypt.annotation.Desensitize;
import com.ty.mid.framework.encrypt.annotation.EncryptField;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.AbstractDesensitizeEncryptor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 加密管理类
 *
 * @author suyouliang
 * @version 4.6.0
 */
@Slf4j
public abstract class AbstractEncryptorManager<T extends Annotation> extends EncryptorManager {

    @Resource
    protected List<AbstractEncryptorManager<T>> encryptorManagerList;

    protected Map<Class<?>, AbstractEncryptorManager<T>> resolverMap;

    public AbstractEncryptorManager(EncryptorConfig defaultProperties) {
        super(defaultProperties);
    }

    @PostConstruct
    public void init() {
        encryptorManagerList.add(this);
        resolverMap = encryptorManagerList.stream().collect(Collectors.toMap((a) -> {
            Class<Object> annotationClass = GenericsUtil.getGenericTypeByIndex(a.getClass(), 0);
            Assert.isTrue(Annotation.class.isAssignableFrom(annotationClass), "{}类型错误,继承AbstractEncryptorManager的类,泛型必须为注解", a.getClass());
            return annotationClass;
        }, Function.identity(), (a, b) -> a));
    }

    /**
     * 字段值进行解密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    @Override
    public String decryptField(String value, Field field) {
        AbstractEncryptorManager<T> route = route(field);
        return route.doDecryptField(value, field);
    }

    @Override
    public List<String> decryptField(Collection<?> value, Field field) {
        //兼容多个字符,通过","拼接的场景
        Collection<String> collectionValue = GenericsUtil.check2Collection(value);
        return collectionValue.stream()
                .map(dataItem -> {
                    AbstractEncryptorManager<T> route = route(field);
                    return route.doDecryptField(dataItem, field);
                }).collect(Collectors.toList());

    }

    @Override
    public List<String> decrypt(Collection<?> value, Annotation annotation) {
        //兼容多个字符,通过","拼接的场景
        Collection<String> collectionValue = GenericsUtil.check2Collection(value);
        return collectionValue.stream()
                .map(dataItem -> {
                    AbstractEncryptorManager<T> route = doRoute(annotation);
                    return route.decrypt(dataItem, annotation);
                }).collect(Collectors.toList());

    }

    @Override
    public String decrypt(String value, Annotation annotation) {
        AbstractEncryptorManager<T> encryptorManager = doRoute(annotation);
        return encryptorManager.doDecrypt(value, annotation);
    }


    @Override
    public String encrypt(String value, Annotation annotation) {
        AbstractEncryptorManager<T> encryptorManager = doRoute(annotation);
        return encryptorManager.doEncrypt(value, annotation);
    }


    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    @Override
    public String encryptField(String value, Field field) {
        AbstractEncryptorManager<T> route = route(field);
        return route.doEncryptField(value, field);
    }

    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    @Override
    public List<String> encryptField(Collection<?> value, Field field) {
        Collection<String> collectionValue = GenericsUtil.check2Collection(value);
        //兼容多个字符,通过","拼接的场景
        return collectionValue.stream()
                .map(dataItem -> {
                    AbstractEncryptorManager<T> route = route(field);
                    return route.doEncryptField(dataItem, field);
                }).collect(Collectors.toList());
    }

    @Override
    public List<String> encrypt(Collection<?> value, Annotation annotation) {
        Collection<String> collectionValue = GenericsUtil.check2Collection(value);
        //兼容多个字符,通过","拼接的场景
        return collectionValue.stream()
                .map(dataItem -> {
                    AbstractEncryptorManager<T> route = doRoute(annotation);
                    return route.doEncrypt(dataItem, annotation);
                }).collect(Collectors.toList());
    }


    public abstract String doDecryptField(String value, Field field);

    public abstract String doDecrypt(String value, Annotation annotation);

    public abstract String doEncryptField(String value, Field field);


    public abstract String doEncrypt(String value, Annotation annotation);

    /**
     * 字段路由,根据字段注解,路由到对应的加密器管理者
     *
     * @param field 待加密字段
     * @return 加密后结果
     */
    public AbstractEncryptorManager<T> route(Field field) {

        Annotation[] annotations = field.getAnnotations();
        Assert.notEmpty(annotations, "属性注解不存在");
        //加密注解暂时只支持同时标注一个,可以是EncryptField注解,或者以EncryptField为元注解的注解
        Optional<Annotation> targetAnnotation = Arrays.stream(field.getAnnotations())
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(EncryptField.class) ||
                        annotation.annotationType().isAssignableFrom(EncryptField.class))
                .findFirst();
        Assert.isTrue(targetAnnotation.isPresent(), "{}未找到加密注解定义", field.getName());
        Annotation annotation = targetAnnotation.get();
        return doRoute(annotation);

    }

    /**
     * 字段路由,根据字段注解,路由到对应的加密器管理者
     *
     * @param annotation 待加密字段的annotation
     * @return 加密后结果
     */
    public AbstractEncryptorManager<T> doRoute(Annotation annotation) {
        if (annotation.annotationType().isAnnotationPresent(Desensitize.class)) {
            return resolverMap.get(Desensitize.class);
        }
        //使用通用方式,通用方式目前支持所有的加密,首先看是否是单向加密的实现,即:AbstractDesensitizeEncryptor的子类
        if (annotation.annotationType().isAssignableFrom(EncryptField.class)) {
            EncryptField encryptFieldAnnotation = (EncryptField) annotation;
            if (AbstractDesensitizeEncryptor.class.isAssignableFrom(encryptFieldAnnotation.algorithm().getClazz())) {
                return resolverMap.get(Desensitize.class);
            }
        }
        //使用普通方式
        AbstractEncryptorManager<T> encryptorManager = resolverMap.get(annotation.annotationType());
        if (Objects.nonNull(encryptorManager)) {
            return encryptorManager;
        }
        throw new FrameworkException(annotation.annotationType() + ",没有对应的注解处理类");

    }

}


