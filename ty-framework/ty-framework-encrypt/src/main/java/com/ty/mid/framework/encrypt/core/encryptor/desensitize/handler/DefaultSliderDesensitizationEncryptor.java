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
public class DefaultSliderDesensitizationEncryptor<S extends Annotation> extends AbstractDesensitizeEncryptor<S> {
    public DefaultSliderDesensitizationEncryptor(DesensitizeEncryptContext context) {
        super(context);
    }

    @Override
    public String desensitize(String origin) {
        int prefixKeep = context.getPrefixKeep();
        int suffixKeep = context.getSuffixKeep();
        String replacer = context.getReplacer();
        int length = origin.length();

        // 情况一：原始字符串长度小于等于保留长度，则原始字符串全部替换
        if (prefixKeep >= length || suffixKeep >= length) {
            return buildReplacerByLength(replacer, length);
        }

        // 情况二：原始字符串长度小于等于前后缀保留字符串长度，则原始字符串全部替换
        if ((prefixKeep + suffixKeep) >= length) {
            return buildReplacerByLength(replacer, length);
        }

        // 情况三：原始字符串长度大于前后缀保留字符串长度，则替换中间字符串
        int interval = length - prefixKeep - suffixKeep;
        return origin.substring(0, prefixKeep) +
                buildReplacerByLength(replacer, interval) +
                origin.substring(prefixKeep + interval);
    }

    /**
     * 根据长度循环构建替换符
     *
     * @param replacer 替换符
     * @param length   长度
     * @return 构建后的替换符
     */
    private String buildReplacerByLength(String replacer, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(replacer);
        }
        return builder.toString();
    }
}
