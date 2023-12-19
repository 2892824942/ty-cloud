package com.ty.mid.framework.lock.handler.lock;

import com.ty.mid.framework.lock.handler.LockHandler;
import com.ty.mid.framework.lock.model.LockInfo;
import org.aspectj.lang.JoinPoint;

import java.util.concurrent.locks.Lock;

/**
 * 获取锁超时的处理逻辑接口
 *
 * @author 苏友良
 * @since 2022/4/15
 **/
public interface ExceptionOnLockHandler extends LockHandler {

    Object handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint, Exception ex);
}
