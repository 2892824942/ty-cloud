package com.ty.mid.framework.api.switcher.deserializer.support;

/**
 * 基于文本文件的开关配置
 *
 * @author suyouliang
 * @createTime 2023-08-14 19:10
 */
public class TextApiSwitcherConfigDeserializer extends AbstractKeyValueApiSwitcherConfigDeserializer {
    public TextApiSwitcherConfigDeserializer() {
    }

    public TextApiSwitcherConfigDeserializer(String fromDateSuffix, String thruDateSuffix, String tipSuffix) {
        super(fromDateSuffix, thruDateSuffix, tipSuffix);
    }

    public TextApiSwitcherConfigDeserializer(String fromDateSuffix, String thruDateSuffix, String tipSuffix, String datePattern) {
        super(fromDateSuffix, thruDateSuffix, tipSuffix, datePattern);
    }

    public TextApiSwitcherConfigDeserializer(String fromDateSuffix, String thruDateSuffix, String tipSuffix, String datePattern, String lineSeparator) {
        super(fromDateSuffix, thruDateSuffix, tipSuffix, datePattern, lineSeparator);
    }
}
