package com.ty.mid.framework.encrypt.core.context;

import cn.hutool.core.lang.Assert;
import com.ty.mid.framework.encrypt.annotation.Desensitize;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler.DefaultRegexDesensitizeEncryptor;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.annotation.Annotation;

/**
 * 加密上下文 用于encryptor传递必要的参数。
 *
 * @author suyouliang
 * @version 4.6.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DesensitizeEncryptContext extends EncryptContext {
    private Annotation sourceAnnotation;

    private Integer prefixKeep;
    private Integer suffixKeep;
    private String replacer;
    private String regex;

    public DesensitizeEncryptContext(int prefixKeep, int suffixKeep, String replacer, Annotation sourceAnnotation) {
        super();
        this.prefixKeep = prefixKeep;
        this.suffixKeep = suffixKeep;
        this.replacer = replacer;
        this.sourceAnnotation = sourceAnnotation;
        Desensitize annotation = sourceAnnotation.annotationType().getAnnotation(Desensitize.class);
        Assert.notNull(annotation, "DesensitizeEncryptContext定义注解必须包含@Desensitize元注解");
        super.setAlgorithm(annotation.handler().isAssignableFrom(DefaultRegexDesensitizeEncryptor.class) ? AlgorithmType.REGEX_DESENSITIZE : AlgorithmType.SLIDER_DESENSITIZE);
    }

    public DesensitizeEncryptContext(String regex, String replacer, Annotation sourceAnnotation) {
        super();
        this.regex = regex;
        this.replacer = replacer;
        this.sourceAnnotation = sourceAnnotation;
        Desensitize annotation = sourceAnnotation.annotationType().getAnnotation(Desensitize.class);
        Assert.notNull(annotation, "DesensitizeEncryptContext定义注解必须包含@Desensitize元注解");
        super.setAlgorithm(annotation.handler().isAssignableFrom(DefaultRegexDesensitizeEncryptor.class) ? AlgorithmType.REGEX_DESENSITIZE : AlgorithmType.SLIDER_DESENSITIZE);

    }
}
