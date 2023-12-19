package com.ty.mid.framework.lock.strategy;

/**
 * 当lock存在于事务上下文中的策略
 */
public enum LockTransactionStrategy {
    /**
     * 生效，打warming日志（仅开启supportTransaction生效）
     */
    WARMING,
    /**
     * 禁止，会抛异常阻断业务逻辑（仅开启supportTransaction生效）
     */
    FORBIDDEN,
    /**
     * 仅开启supportTransaction生效，
     * 保证多线程访问安全，unlock会在事务完成后再提交。
     * 缺点：会使lock的作用域膨胀。直至上下文事务完成
     */
    THREAD_SAFE;
}
