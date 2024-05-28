package com.ty.mid.framework.idempotent.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

/**
 * 幂等校验结果上下文 <p>
 *
 * @author suyouliang <p>
 * @createTime 2023-08-15 15:09
 */
public class IdempotentContextHolder {
    private static final Logger log = LoggerFactory.getLogger(IdempotentContextHolder.class);

    private static ThreadLocal<Stack<Boolean>> threadLocal = new InheritableThreadLocal<>();

    static void push(boolean executed) {
        ensureNonNull();

        threadLocal.get().push(executed);
    }

    public static boolean isCurrentExecuted() {
        ensureNonNull();

        if (threadLocal.get().isEmpty()) {
            log.warn("warning: there is no idempotent check result in current context");
            return false;
        }

        return threadLocal.get().peek();
    }

    static void pop() {
        ensureNonNull();

        if (threadLocal.get().isEmpty()) {
            log.warn("warning: there is no idempotent check result in current context");
            return;
        }

        threadLocal.get().pop();
    }

    private static void ensureNonNull() {
        if (threadLocal == null) {
            threadLocal = new InheritableThreadLocal<>();
        }

        if (threadLocal.get() == null) {
            threadLocal.set(new Stack<>());
        }
    }
}
