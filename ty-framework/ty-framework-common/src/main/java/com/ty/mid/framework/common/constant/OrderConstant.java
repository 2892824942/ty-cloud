package com.ty.mid.framework.common.constant;

/**
 * 越小,优先级越高
 */
public interface OrderConstant {
    /**
     * 全局的异常处理器作为兜底,应放到最后
     */
    int GLOBAL_EXCEPTION_HANDLER = Integer.MAX_VALUE - 10;
    int SECURITY_EXCEPTION_HANDLER = 0;
}
