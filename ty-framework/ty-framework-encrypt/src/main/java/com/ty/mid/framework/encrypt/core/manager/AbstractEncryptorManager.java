package com.ty.mid.framework.encrypt.core.manager;

import cn.hutool.core.lang.Assert;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.encrypt.annotation.Desensitize;
import com.ty.mid.framework.encrypt.annotation.EncryptField;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

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

    @PostConstruct
    public void init() {
        encryptorManagerList.add(this);
        resolverMap = encryptorManagerList.stream().collect(Collectors.toMap((a) -> {
            Class<Object> annotationClass = GenericsUtil.getGenericTypeByIndex(a.getClass(), 0);
            Assert.isTrue(Annotation.class.isAssignableFrom(annotationClass), "{}类型错误,继承AbstractEncryptorManager的类,泛型必须为注解", a.getClass());
            return annotationClass;
        }, Function.identity(), (a, b) -> a));
    }

    public AbstractEncryptorManager(EncryptorConfig defaultProperties) {
        super(defaultProperties);
    }

    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
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
    public String encryptField(String value, Field field) {
        AbstractEncryptorManager<T> route = route(field);
        return route.doEncryptField(value, field);
    }

    public abstract String doDecryptField(String value, Field field);

    public abstract String doEncryptField(String value, Field field);

    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param field 待加密字段
     * @return 加密后结果
     */
    public AbstractEncryptorManager<T> route(Field field) {

        Annotation[] annotations = field.getAnnotations();
        Assert.notEmpty(annotations, "属性注解不存在");
        //加密注解暂时只支持同时标注一个
        Optional<Annotation> targetAnnotation = Arrays.stream(field.getAnnotations()).filter(annotation -> annotation.annotationType().isAnnotationPresent(EncryptField.class)).findFirst();
        Assert.isTrue(targetAnnotation.isPresent(), "{}未找到加密注解定义", field.getName());
        Annotation annotation = targetAnnotation.get();
        if (annotation.annotationType().isAnnotationPresent(Desensitize.class)) {
            return resolverMap.get(Desensitize.class);
        }
        AbstractEncryptorManager<T> encryptorManager = resolverMap.get(annotation.annotationType());
        if (Objects.nonNull(encryptorManager)) {
            return encryptorManager;
        }
        throw new FrameworkException(annotation.annotationType() + ",没有对应的注解处理类");

    }

}


