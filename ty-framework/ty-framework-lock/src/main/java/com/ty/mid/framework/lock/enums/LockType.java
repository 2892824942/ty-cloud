package com.ty.mid.framework.lock.enums;

/**
 * Created by suyouliang
 * Content :锁类型
 */
public enum LockType {
    /**
     * 可重入锁
     */
    Reentrant("reentrant"),
    /**
     * 公平锁
     */
    Fair("fair"),
    /**
     * 读锁
     */
    Read("read"),
    /**
     * 写锁
     */
    Write("write"),
    /**
     * 自旋锁
     */
    Spin("spin"),
    ;
    private String code;

    LockType(String code) {
        this.code = code;
    }

    public static LockType of(String code) {
        for (LockType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }


}
