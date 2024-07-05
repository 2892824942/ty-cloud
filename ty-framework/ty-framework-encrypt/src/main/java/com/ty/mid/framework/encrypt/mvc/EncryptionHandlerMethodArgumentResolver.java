package com.ty.mid.framework.encrypt.mvc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.HashIdUtil;
import com.ty.mid.framework.encrypt.annotation.Desensitize;
import com.ty.mid.framework.encrypt.annotation.EncryptField;
import com.ty.mid.framework.encrypt.annotation.HashedId;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.manager.EncryptorManager;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 对于表单形式,不经过序列化,入参情况下注解解密处理
 */
@Slf4j
@NoArgsConstructor
public class EncryptionHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    EncryptorManager encryptorManager;
    public EncryptionHandlerMethodArgumentResolver(EncryptorManager encryptorManager) {
        this.encryptorManager = encryptorManager;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //TODO 加缓存优化下?
        Annotation[] parameterAnnotations = parameter.getParameterAnnotations();
        return Arrays.stream(parameterAnnotations).anyMatch(annotation -> annotation.annotationType().isAnnotationPresent(EncryptField.class) ||
                            annotation.annotationType().isAssignableFrom(EncryptField.class));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        String parameterName = parameter.getParameterName();
        if (StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("parameterName must not be blank!");
        }
        Annotation[] parameterAnnotations = parameter.getParameterAnnotations();
        Optional<Annotation> encryptAnnotationOpt = Arrays.stream(parameterAnnotations).filter(annotation -> annotation.annotationType().isAnnotationPresent(EncryptField.class) ||
                annotation.annotationType().isAssignableFrom(EncryptField.class)).findFirst();
        if (!encryptAnnotationOpt.isPresent()){
            throw new FrameworkException("未找到有效的加密注解");
        }
        Annotation annotation = encryptAnnotationOpt.get();
        log.debug("resolving encryption, parameter name: {}", parameterName);
        Class<?> parameterType = parameter.getParameterType();
        if (String.class.isAssignableFrom(parameterType)) {
            String val = webRequest.getParameter(parameterName);
            if (StringUtils.isEmpty(val)) {
                log.warn("resolve origin data for [p:{} ,v:{}] fail, because parameter value is null or empty!", parameterName, val);
                return val;
            }
            //兼容多个加密id使用","隔开的情况
            if (val.contains(",") && val.split(",").length >= 1) {
                StringJoiner stringJoiner = new StringJoiner(",");
                String[] split = val.split(",");
                List<String> list = Arrays.asList(split);
                List<String> decryptValueList = encryptorManager.decrypt(list, annotation);
                if (CollUtil.isEmpty(decryptValueList)){
                    return val;
                }
                decryptValueList.forEach(stringJoiner::add);
                return stringJoiner.toString();
            }
            //单纯的字符串
            encryptorManager.decrypt(val, annotation);
        }
        //数组形式
        if (List.class.isAssignableFrom(parameter.getParameterType()) || String[].class.isAssignableFrom(parameter.getParameterType())) {
            String[] parameterValues = webRequest.getParameterValues(parameterName);
            if (ArrayUtil.isNotEmpty(parameterValues)) {
                return Arrays.stream(parameterValues)
                        .map(data ->   encryptorManager.decrypt(data, annotation))
                        .collect(Collectors.toList());
            }
        }
        //其他形式
        throw new FrameworkException("暂只支持String及List<Long>形式加解密!");
    }
}
