package com.ty.mid.framework.core.aspect;

import com.ty.mid.framework.common.lang.NonNull;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * 抽象切面，为切面开发提供支持 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-15 14:13
 */
public abstract class AbstractAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 获取切点方法
     *
     * @param joinPoint
     * @return
     */
    protected Method resolveMethod(@NonNull JoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    /**
     * 获取方法上的注解
     *
     * @param annotatedElement
     * @param annotationClass
     * @param <T>
     * @return
     */
    protected <T extends Annotation> T findAnnotation(@NonNull AnnotatedElement annotatedElement, @NonNull Class<T> annotationClass) {
        long start = System.currentTimeMillis();
        T annotation = AnnotationUtils.findAnnotation(annotatedElement, annotationClass);
        long end = System.currentTimeMillis();

        //log.debug("find annotation on {}, used {}ms, is annotation find: {}", annotatedElement.toString(), (end - start), annotatedElement != null);

        return annotation;
    }

}