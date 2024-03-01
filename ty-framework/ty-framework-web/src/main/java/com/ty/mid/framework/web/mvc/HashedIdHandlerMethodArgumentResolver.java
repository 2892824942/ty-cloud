package com.ty.mid.framework.web.mvc;

import com.ty.mid.framework.web.annotation.desensitize.HashedId;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.HashIdUtil;
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

import java.util.StringJoiner;

@Slf4j
@NoArgsConstructor
public class HashedIdHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private WebConfig webConfig;

    public HashedIdHandlerMethodArgumentResolver(WebConfig webConfig) {
        this.webConfig = webConfig;
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
        String val = webRequest.getParameter(parameterName);

        if (StringUtils.isEmpty(val)) {
            log.warn("resolve origin id for [p:{} ,v:{}] fail, because parameter value is null or empty!", parameterName, val);
            return null;
        }

        WebConfig.HashId hashId = webConfig.getHashId();
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
                        throw new FrameworkException("加解密异常");
                    }

                }
            }
            return stringJoiner.toString();
        }
        if (parameter.getParameterType().isAssignableFrom(String.class)) {
            return String.valueOf(HashIdUtil.decode(val, hashId.getSalt(), hashId.getMinLength()));
        }
        return HashIdUtil.decode(val, hashId.getSalt(), hashId.getMinLength());
    }
}
