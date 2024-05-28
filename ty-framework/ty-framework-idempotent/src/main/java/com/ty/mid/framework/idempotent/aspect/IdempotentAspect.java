package com.ty.mid.framework.idempotent.aspect;

import com.ty.mid.framework.core.aspect.AbstractAspect;
import com.ty.mid.framework.core.expression.ExpressionManager;
import com.ty.mid.framework.core.expression.support.DefaultExpressionManager;
import com.ty.mid.framework.idempotent.annotation.Idempotent;
import com.ty.mid.framework.idempotent.exception.AlreadyExecutedIdempotentException;
import com.ty.mid.framework.idempotent.service.IdempotentService;
import com.ty.mid.framework.idempotent.strategy.IdempotentRejectStrategy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 幂等校验切面 <p>
 *
 * @author suyouliang <p>
 * @createTime 2023-08-15 14:21
 */
@Aspect
@Slf4j
public class IdempotentAspect extends AbstractAspect {

    // 幂等检查服务
    private IdempotentService idempotentService;

    private ExpressionManager expressionManager;

    public IdempotentAspect(IdempotentService idempotentService) {
        this(idempotentService, DefaultExpressionManager.INSTANCE);
    }

    public IdempotentAspect(IdempotentService idempotentService, ExpressionManager expressionManager) {
        this.idempotentService = idempotentService;
        this.expressionManager = expressionManager;
    }

    @Pointcut("@annotation(com.zhipin.retail.framework.idempotent.annotation.Idempotent)")
    public void idempotentPointcut() {
    }

    @Around("idempotentPointcut()")
    public Object checkApiIdempotent(ProceedingJoinPoint jp) throws Throwable {
        Method method = this.resolveMethod(jp);
        log.info("check api idempotent from class: {}, method: {}", method.getDeclaringClass().getName(), method.getName());

        // get annotation
        Idempotent annotation = super.findAnnotation(method, Idempotent.class);
        if (StringUtils.isEmpty(annotation)) {
            log.warn("{}.{} is idempotent annotation has empty value, so skip idempotent check", method.getDeclaringClass().getName(), method.getName());
            return jp.proceed();
        }

        // resolve identifier
        String identifier = this.resolveIdentifier(annotation, method, jp.getArgs(), jp.getTarget());
        if (StringUtils.isEmpty(identifier)) {
            log.warn("can not resolve identifier from {}.{} by {}", method.getDeclaringClass().getName(), method.getName(), annotation.value());
            return jp.proceed();
        }

        // check
        this.doIdempotentCheck(identifier, annotation, method);

        // invoke target
        try {
            return jp.proceed();
        } catch (Exception e) {
            throw e;
        } finally {
            if (annotation.rejectStrategy() == IdempotentRejectStrategy.PUSH_CONTEXT) {
                IdempotentContextHolder.pop();
            }
        }
    }

    /**
     * 执行幂等性校验
     *
     * @param identifier
     * @param idempotent
     * @param method
     */
    protected void doIdempotentCheck(String identifier, Idempotent idempotent, Method method) {

        boolean isExecuted = this.idempotentService.isServiceExecuted(identifier);
        if (isExecuted) {
            log.warn("{}.{} with identifier {} is already executed!", method.getDeclaringClass().getName(), method.getName(), identifier);
            this.rejectByStrategy(identifier, idempotent, method);
            return;
        }

        this.idempotentService.markServiceExecuted(identifier);
    }

    protected void rejectByStrategy(String identifier, Idempotent idempotent, Method method) {
        if (IdempotentRejectStrategy.THROW_EXCEPTION == idempotent.rejectStrategy()) {
            // throw exception
            throw new AlreadyExecutedIdempotentException(String.format("identifier %s on %s.%s is already executed", identifier, method.getDeclaringClass().getName(), method.getName()));
        } else {
            IdempotentContextHolder.push(true);
        }
    }

    /**
     * 根据表达式，获取业务标识符
     *
     * @param annotation
     * @param method
     * @param args
     * @param target
     * @return
     */
    protected String resolveIdentifier(Idempotent annotation, Method method, Object[] args, Object target) {
        return this.expressionManager.evaluateMethodBasedString(annotation.value(), method, args, target);
    }

    protected static class IdempotentRootObject {
        private Object target;
        private Object[] args;

        public IdempotentRootObject(Object target, Object[] args) {
            this.target = target;
            this.args = args;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }
    }

}
