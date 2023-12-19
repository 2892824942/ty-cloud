package com.ty.mid.framework.lock.handler.release;


import com.ty.mid.framework.lock.handler.LockHandler;
import com.ty.mid.framework.lock.model.LockInfo;

/**
 * 获取锁超时的处理逻辑接口
 *
 * @author 苏友良
 * @since 2022/4/15
 **/
public interface ReleaseTimeoutHandler extends LockHandler {

    void handle(LockInfo lockInfo);
}
