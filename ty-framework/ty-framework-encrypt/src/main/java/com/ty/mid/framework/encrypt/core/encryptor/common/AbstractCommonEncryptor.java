package com.ty.mid.framework.encrypt.core.encryptor.common;

import com.ty.mid.framework.encrypt.core.context.CommonEncryptContext;
import com.ty.mid.framework.encrypt.core.encryptor.AbstractEncryptor;

/**
 * 所有加密执行者的基类
 *
 * @author suyouliang
 * @version 4.6.0
 */
public abstract class AbstractCommonEncryptor extends AbstractEncryptor<CommonEncryptContext> {
    protected final CommonEncryptContext context;

    public AbstractCommonEncryptor(CommonEncryptContext context) {
        super(context);
        this.context = context;
        // 用户配置校验与配置注入
    }

}
