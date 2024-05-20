package com.ty.mid.framework.encrypt.core.encryptor;

import com.ty.mid.framework.encrypt.core.EncryptContext;
import com.ty.mid.framework.encrypt.core.IEncryptor;

/**
 * 所有加密执行者的基类
 *
 * @author suyouliang
 * @version 4.6.0
 */
public abstract class AbstractEncryptor implements IEncryptor {

    public AbstractEncryptor(EncryptContext context) {
        // 用户配置校验与配置注入
    }

}
