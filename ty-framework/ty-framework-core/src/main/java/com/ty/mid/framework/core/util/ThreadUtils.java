package com.ty.mid.framework.core.util;

public abstract class ThreadUtils {


    /**
     * 获取当前线程名称
     *
     * @return
     */
    public static String getCurrentThreadNameSafety() {
        Thread thread = getCurrentThread();
        return null == thread ? null : thread.getName();
    }

    /**
     * 获取当前线程组名称
     *
     * @return
     */
    public static String getCurrentThreadGroupNameSafety() {
        Thread thread = Thread.currentThread();

        return null == thread ? null :
                null == thread.getThreadGroup() ? null : thread.getThreadGroup().getName();
    }

    /**
     * 获取当前线程
     *
     * @return
     */
    public static Thread getCurrentThread() {
        return Thread.currentThread();
    }

}
