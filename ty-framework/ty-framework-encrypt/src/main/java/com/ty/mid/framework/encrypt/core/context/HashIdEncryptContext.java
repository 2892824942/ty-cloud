package com.ty.mid.framework.encrypt.core.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 加密上下文 用于encryptor传递必要的参数。
 *
 * @author suyouliang
 * @version 4.6.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HashIdEncryptContext extends EncryptContext {

    /**
     * 自定义盐
     */
    private String salt = "helloWorld123";

    /**
     * 最小长度
     */
    private int minLength = 6;

}
