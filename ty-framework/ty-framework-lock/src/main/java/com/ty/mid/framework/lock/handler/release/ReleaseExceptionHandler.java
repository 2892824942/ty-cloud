package com.ty.mid.framework.lock.handler.release;


import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.handler.LockHandler;
import org.aspectj.lang.JoinPoint;

import java.util.concurrent.locks.Lock;

/**
 * 获取锁超时的处理逻辑接口
 *
 * @author 苏友良
 * @since 2022/4/15
 **/
public interface ReleaseExceptionHandler extends LockHandler {

    void handle(LockInfo lockInfo, Lock lock, Exception ex);

    void handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint, Exception ex);
}
