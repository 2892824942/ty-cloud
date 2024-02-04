package com.ty.mid.framework.lock.handler.release;


import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.handler.LockHandler;

/**
 * 获取锁超时的处理逻辑接口
 *
 * @author 苏友良
 * @since 2022/4/15
 **/
public interface ReleaseTimeoutHandler extends LockHandler {

    void handle(LockInfo lockInfo);
}
