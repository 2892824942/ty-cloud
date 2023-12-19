package com.ty.mid.framework.web.advice;

import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.web.model.WebResult;
import com.ty.mid.framework.web.util.R;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class AbstractRestControllerAdvice {

    @ExceptionHandler(FrameworkException.class)
    public WebResult<?> onFrameworkException(FrameworkException e) {
        return R.ofFail(e);
    }

    @ExceptionHandler(Exception.class)
    public WebResult<?> onException(Exception e) {
        return R.ofFail(e);
    }

}
