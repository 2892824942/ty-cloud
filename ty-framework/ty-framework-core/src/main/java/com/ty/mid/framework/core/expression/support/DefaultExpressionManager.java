package com.ty.mid.framework.core.expression.support;

import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.core.expression.ExpressionManager;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author suyouliang
 * @createDate 2021/3/31
 */
public class DefaultExpressionManager implements ExpressionManager {

    public static final ExpressionManager INSTANCE = new DefaultExpressionManager();

    private Map<String, Expression> expressionCache = new ConcurrentHashMap<>(64);

    private ParserContext parserContext = new TemplateParserContext();
    private ExpressionParser expressionParser = new SpelExpressionParser();
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private boolean enableEnvAccess = true;

    public DefaultExpressionManager() {
    }

    public DefaultExpressionManager(ParserContext parserContext, ExpressionParser expressionParser, ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parserContext = parserContext;
        this.expressionParser = expressionParser;
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Override
    public <T> T evaluate(String expression, Object root, Class<T> resultClass) {
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        initPropertyAccessors(context);

        Expression exp = this.parseExpression(expression);
        return exp.getValue(context, resultClass);
    }

    @Override
    public <T> T evaluate(String expression, Map root, Class<T> resultClass) {
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        initPropertyAccessors(context);

        Expression exp = this.parseExpression(expression);
        return exp.getValue(context, resultClass);
    }

    @Override
    public <T> T evaluateMethodBased(String expression, Method method, Object[] args, Object target, Class<T> resultClass) {
        Expression exp = this.parseExpression(expression);
        MethodBasedEvaluationContext ctx = new MethodBasedEvaluationContext(new MethodBasedRootObject(target, method, args), method, args, this.parameterNameDiscoverer);
        initPropertyAccessors(ctx);

        return exp.getValue(ctx, resultClass);
    }

    protected Expression parseExpression(String expression) {
        Validator.requireNonEmpty(expression, "expression 不能为空");

        if (!expressionCache.containsKey(expression)) {
            synchronized (expressionCache) {
                if (!expressionCache.containsKey(expression)) {
                    Expression exp = this.expressionParser.parseExpression(expression, parserContext);
                    expressionCache.put(expression, exp);
                }
            }
        }

        return expressionCache.get(expression);
    }

    protected void initPropertyAccessors(StandardEvaluationContext context) {
        context.addPropertyAccessor(new MapAccessor());
        if (enableEnvAccess) {
            context.addPropertyAccessor(new EnvironmentAccessor());
        }
    }

    public static class MethodBasedRootObject {
        private Object target;
        private Method method;
        private Object[] args;

        public MethodBasedRootObject() {
        }

        public MethodBasedRootObject(Object target, Method method, Object[] args) {
            this.target = target;
            this.method = method;
            this.args = args;
        }

        public Object getTarget() {
            return target;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Object[] getArgs() {
            return args;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }
    }

}
