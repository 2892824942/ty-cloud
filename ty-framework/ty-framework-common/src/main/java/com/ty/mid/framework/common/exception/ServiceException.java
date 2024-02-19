package com.ty.mid.framework.common.exception;

import com.ty.mid.framework.common.constant.BaseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务逻辑异常 Exception
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ServiceException extends BaseException {


    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    public static ServiceException of(BaseCode baseCode){
        return new ServiceException(baseCode);
    }

    public static ServiceException of(String code, String message){
        return new ServiceException(code,message);
    }

    public static ServiceException of(String message){
        return new ServiceException(message);
    }

    public ServiceException(Integer code, String message) {
        this(String.valueOf(code), message);
    }

    public ServiceException(BaseCode baseCode) {
        super(baseCode.getMessage());
        super.code = baseCode.getCode();
    }



    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(String message) {
        super(message);
    }


}
