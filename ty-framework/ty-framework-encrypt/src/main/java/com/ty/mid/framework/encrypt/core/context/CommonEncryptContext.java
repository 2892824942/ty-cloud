package com.ty.mid.framework.encrypt.core.context;

import com.ty.mid.framework.encrypt.enumd.EncodeType;
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
public class CommonEncryptContext extends EncryptContext {

    /**
     * 安全秘钥
     */
    private String password;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

}
