package com.ty.mid.framework.web.annotation.desensitize.handler;


import java.lang.annotation.Annotation;

/**
 * 正则表达式脱敏处理器抽象类，已实现通用的方法 <p>
 *
 * @author suyouliang
 */
public abstract class AbstractRegexDesensitizationHandler<S extends Annotation>
        implements DesensitizationHandler<S> {

    @Override
    public String desensitize(String origin, S annotation) {
        DesensitizeContext desensitizeInfo = DesensitizeEnum.toDesensitizeInfo(annotation);
        String regex = desensitizeInfo.getRegex();
        String replacer = desensitizeInfo.getReplacer();
        return origin.replaceAll(regex, replacer);
    }

}
