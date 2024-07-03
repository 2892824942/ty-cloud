package com.ty.mid.framework.encrypt.core.encryptor;

import com.ty.mid.framework.encrypt.core.IEncryptor;
import com.ty.mid.framework.encrypt.core.context.EncryptContext;
import lombok.NoArgsConstructor;

/**
 * 所有加密执行者的基类
 *
 * @author suyouliang
 * @version 4.6.0
 */
@NoArgsConstructor
public abstract class AbstractEncryptor<T extends EncryptContext> implements IEncryptor {

    public AbstractEncryptor(T context) {
        // 用户配置校验与配置注入
    }

}
