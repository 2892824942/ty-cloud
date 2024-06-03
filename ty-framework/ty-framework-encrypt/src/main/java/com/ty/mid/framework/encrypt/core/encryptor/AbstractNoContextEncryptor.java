package com.ty.mid.framework.encrypt.core.encryptor;

import com.ty.mid.framework.encrypt.core.context.EncryptContext;

/**
 * 所有加密执行者的基类
 *
 * @author suyouliang
 * @version 4.6.0
 */
public abstract class AbstractNoContextEncryptor extends AbstractEncryptor<EncryptContext> {

    public AbstractNoContextEncryptor() {
        super(null);
        // 用户配置校验与配置注入
    }

}
