package com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler;


import com.ty.mid.framework.encrypt.annotation.SliderDesensitize;
import com.ty.mid.framework.encrypt.core.context.DesensitizeEncryptContext;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.AbstractDesensitizeEncryptor;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

/**
 * {@link SliderDesensitize} 的脱敏处理器 <p>
 *
 * @author suyouliang
 */
@NoArgsConstructor
public class DefaultRegexDesensitizeEncryptor<S extends Annotation> extends AbstractDesensitizeEncryptor<S> {

    public DefaultRegexDesensitizeEncryptor(DesensitizeEncryptContext context) {
        super(context);
    }
}
