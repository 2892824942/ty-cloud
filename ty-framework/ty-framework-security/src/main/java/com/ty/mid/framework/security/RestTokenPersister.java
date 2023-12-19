package com.ty.mid.framework.security;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface RestTokenPersister<T> {

    /**
     * 删除token
     *
     * @param principal
     */
    void remove(String principal);

    /**
     * 获取token
     *
     * @param principal
     * @return
     */
    String get(String principal);

    /**
     * 保存token
     *
     * @param principal
     * @return
     */
    String save(String principal, String token);

    Date getExpire(String principal);

    /**
     * 续租 token
     *
     * @param t
     * @param renewTime
     * @param timeUnit
     * @return
     */
    boolean renew(T t, long renewTime, TimeUnit timeUnit);
}
