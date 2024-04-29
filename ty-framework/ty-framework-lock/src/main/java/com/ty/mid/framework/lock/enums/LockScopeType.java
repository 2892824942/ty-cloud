package com.ty.mid.framework.lock.enums;

/**
 * @author 苏友良 
 */
public enum LockScopeType {
    /**
     * 分布式锁
     */
    Distributed,
    /**
     * 本地锁
     */
    Local;

    LockScopeType() {
    }

}
