package com.ty.mid.framework.web.mvc;

import cn.hutool.core.util.ArrayUtil;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.HashIdUtil;
import com.ty.mid.framework.encrypt.annotation.HashedId;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.web.config.WebConfig;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 对于表单形式,不经过序列化,入参情况下HashId注解解密处理
 * 注:方法注入时已经判断开启Hashed能力,所以方法直接进行数据操作
 */
@Slf4j
@NoArgsConstructor
public class HashedIdHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private EncryptorConfig encryptorConfig;

    public HashedIdHandlerMethodArgumentResolver(EncryptorConfig encryptorConfig) {
        this.encryptorConfig = encryptorConfig;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(HashedId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        String parameterName = parameter.getParameterName();
        if (StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("parameterName must not be blank!");
        }
        log.info("resolving hashed id, parameter name: {}", parameterName);
        Class<?> parameterType = parameter.getParameterType();
        EncryptorConfig.HashId hashId = encryptorConfig.getHashId();
        if (String.class.isAssignableFrom(parameterType)) {
            String val = webRequest.getParameter(parameterName);
            if (StringUtils.isEmpty(val)) {
                log.warn("resolve origin id for [p:{} ,v:{}] fail, because parameter value is null or empty!", parameterName, val);
                return null;
            }

            //兼容多个加密id使用","隔开的情况
            if (val.contains(",") && val.split(",").length >= 1) {
                StringJoiner stringJoiner = new StringJoiner(",");
                String[] split = val.split(",");
                for (String s : split) {
                    if (StringUtils.isEmpty(s)) {
                        stringJoiner.add("");
                    } else {
                        try {
                            stringJoiner.add(String.valueOf(HashIdUtil.decode(s, hashId.getSalt(), hashId.getMinLength())));
                        } catch (Exception e) {
                            log.warn("resolve origin id for [p:{} ,v:{}] fail, because of parameter value decode error!", parameterName, val);
                            throw new FrameworkException("加解密异常[HashedId]");
                        }
                    }
                }
                return stringJoiner.toString();
            }
            //单纯的字符串
            return String.valueOf(HashIdUtil.decode(val, hashId.getSalt(), hashId.getMinLength()));
        }
        //数组形式
        if (List.class.isAssignableFrom(parameter.getParameterType()) || String[].class.isAssignableFrom(parameter.getParameterType())) {
            String[] parameterValues = webRequest.getParameterValues(parameterName);
            if (ArrayUtil.isNotEmpty(parameterValues)) {
                return Arrays.stream(parameterValues)
                        .map(data -> HashIdUtil.decode(data, hashId.getSalt(), hashId.getMinLength()))
                        .collect(Collectors.toList());
            }
        }

        //其他形式
        throw new FrameworkException("暂只支持String及List<Long>形式![HashedId]");
    }
}
