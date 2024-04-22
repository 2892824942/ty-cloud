package com.ty.mid.framework.idempotent.service;

import com.ty.mid.framework.idempotent.exception.IdempotentServiceException;

/**
 * 幂等性校验服务
 *
 * @author suyouliang
 * @createTime 2023-08-15 14:28
 */
public interface IdempotentService {

    /**
     * 标记服务已经执行过
     *
     * @param identifier
     */
    void markServiceExecuted(String identifier) throws IdempotentServiceException;

    /**
     * 获取标记是否已经执行过
     *
     * @param identifier
     * @return
     */
    boolean isServiceExecuted(String identifier);

}
