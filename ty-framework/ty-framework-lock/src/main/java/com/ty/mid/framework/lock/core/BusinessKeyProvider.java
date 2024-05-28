package com.ty.mid.framework.lock.core;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.common.util.SafeGetUtil;
import com.ty.mid.framework.lock.annotation.Lock;
import com.ty.mid.framework.lock.annotation.LockKey;
import com.ty.mid.framework.lock.config.LockConfig;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suyouliang on 2018/1/24. <p>
 * Content :获取用户定义业务key
 */
@RequiredArgsConstructor
public class BusinessKeyProvider {
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    private final ExpressionParser parser = new SpelExpressionParser();
    private final LockConfig lockConfig;

    public String getKeyName(JoinPoint joinPoint, Lock lock) {
        Method method = getMethod(joinPoint);
        List<String> keyList = new ArrayList<>();
        //名称为空，不做任何处理
        if (StrUtil.isNotEmpty(lock.name())) {
            keyList.add(lock.name());
        }
        List<String> definitionKeys = getSpelDefinitionKey(lock.keys(), method, joinPoint.getArgs());
        keyList.addAll(definitionKeys);
        List<String> parameterKeys = getParameterKey(method.getParameters(), joinPoint.getArgs());
        keyList.addAll(parameterKeys);
        return StringUtils.collectionToDelimitedString(keyList, SafeGetUtil.getString(lockConfig.getLockNameSeparator()));
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(),
                        method.getParameterTypes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return method;
    }

    private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {
        List<String> definitionKeyList = new ArrayList<>();
        for (String definitionKey : definitionKeys) {
            if (!ObjectUtils.isEmpty(definitionKey)) {
                EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, nameDiscoverer);
                Object objKey = parser.parseExpression(definitionKey).getValue(context);
                definitionKeyList.add(ObjectUtils.nullSafeToString(objKey));
            }
        }
        return definitionKeyList;
    }

    private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {
        List<String> parameterKey = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getAnnotation(LockKey.class) != null) {
                LockKey keyAnnotation = parameters[i].getAnnotation(LockKey.class);
                Object parameterValue;
                if (keyAnnotation.value().isEmpty()) {
                    parameterValue = parameterValues[i];
                } else {
                    StandardEvaluationContext context = new StandardEvaluationContext(parameterValues[i]);
                    parameterValue = parser.parseExpression(keyAnnotation.value()).getValue(context);

                }
                //用户指定的参数，如果为空，使用"null"代替，为了更显眼。
                parameterKey.add(ObjectUtils.nullSafeToString(parameterValue));
            }
        }
        return parameterKey;
    }
}
