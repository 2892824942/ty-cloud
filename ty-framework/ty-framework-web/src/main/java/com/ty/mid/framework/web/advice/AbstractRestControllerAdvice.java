package com.ty.mid.framework.web.advice;

import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.pojo.BaseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class AbstractRestControllerAdvice {

    @ExceptionHandler(FrameworkException.class)
    public BaseResult<?> onFrameworkException(FrameworkException e) {
        return BaseResult.fail(e);
    }

    @ExceptionHandler(Exception.class)
    public BaseResult<?> onException(Exception e) {
        return BaseResult.fail(e);
    }

}
