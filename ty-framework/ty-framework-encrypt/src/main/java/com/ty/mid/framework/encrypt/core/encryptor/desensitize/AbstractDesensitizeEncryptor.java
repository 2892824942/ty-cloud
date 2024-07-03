package com.ty.mid.framework.encrypt.core.encryptor.desensitize;

import com.ty.mid.framework.encrypt.core.context.DesensitizeEncryptContext;
import com.ty.mid.framework.encrypt.core.encryptor.AbstractEncryptor;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler.DesensitizationHandler;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

/**
 * 所有加密执行者的基类
 *
 * @author suyouliang
 * @version 4.6.0
 */
@Getter
@NoArgsConstructor
public abstract class AbstractDesensitizeEncryptor<S extends Annotation> extends AbstractEncryptor<DesensitizeEncryptContext> implements DesensitizationHandler<S> {

    /**
     * 脱敏
     */
    protected DesensitizeEncryptContext context;

    public AbstractDesensitizeEncryptor(DesensitizeEncryptContext context) {
        super(context);
        this.context = context;
        // 用户配置校验与配置注入
    }

    public void setContext(DesensitizeEncryptContext context) {
        this.context = context;
    }

    @Override
    public AlgorithmType algorithm() {
        return context.getAlgorithm();
    }

    @Override
    public String encrypt(String value, EncodeType encodeType) {
        return desensitize(value);
    }

    @Override
    public String decrypt(String value) {
        return value;
    }

    @Override
    public String desensitize(String origin) {
        String regex = context.getRegex();
        String replacer = context.getReplacer();
        return origin.replaceAll(regex, replacer);
    }

}
