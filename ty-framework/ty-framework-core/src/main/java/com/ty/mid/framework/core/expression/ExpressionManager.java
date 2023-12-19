package com.ty.mid.framework.core.expression;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author suyouliang
 * @createDate 2021/3/31
 */
public interface ExpressionManager {

    default String evaluateString(String expression, Object root) {
        return this.evaluate(expression, root, String.class);
    }

    default String evaluateString(String expression, Map root) {
        return this.evaluate(expression, root, String.class);
    }

    default String evaluateMethodBasedString(String expression, Method method, Object[] args, Object target) {
        return this.evaluateMethodBased(expression, method, args, target, String.class);
    }

    <T> T evaluate(String expression, Object root, Class<T> resultClass);

    <T> T evaluate(String expression, Map root, Class<T> resultClass);

    <T> T evaluateMethodBased(String expression, Method method, Object[] args, Object target, Class<T> resultClass);

}
