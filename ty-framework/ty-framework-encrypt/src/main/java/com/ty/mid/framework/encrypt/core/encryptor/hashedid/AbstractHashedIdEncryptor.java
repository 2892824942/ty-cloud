package com.ty.mid.framework.encrypt.core.encryptor.hashedid;

import com.ty.mid.framework.encrypt.core.context.CommonEncryptContext;
import com.ty.mid.framework.encrypt.core.context.HashIdEncryptContext;
import com.ty.mid.framework.encrypt.core.encryptor.AbstractEncryptor;

/**
 * 所有加密执行者的基类
 *
 * @author suyouliang
 * @version 4.6.0
 */
public abstract class AbstractHashedIdEncryptor extends AbstractEncryptor<HashIdEncryptContext> {
    protected final HashIdEncryptContext context;

    public AbstractHashedIdEncryptor(HashIdEncryptContext context) {
        super(context);
        this.context = context;
        // 用户配置校验与配置注入
    }

}
