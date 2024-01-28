package com.ty.mid.framework.lock.enums;

/**
 * 当lock存在于事务上下文中的策略
 */
public enum LockImplementer {
    /**
     * 本地JVM实现
     */
    JVM,
    /**

    /**
     * redis实现
     */
    REDIS,
    /**
     * zookeeper实现
     */
    ZOOKEEPER,
    /**
     * etce实现
     */
    ETCD,
    /**
     * mysql实现
     */
    MYSQL,
    ;
}
