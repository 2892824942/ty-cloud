package com.ty.mid.framework.common.util;

import cn.hutool.core.lang.Assert;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.lang.NonNull;

public abstract class ExceptionUtils {

    public static FrameworkException wrapFramework(@NonNull Throwable throwable) {
        Assert.notNull(throwable);

        if (throwable instanceof FrameworkException) {
            return (FrameworkException) throwable;
        }

        return new FrameworkException(throwable.getMessage(), throwable);
    }

}
