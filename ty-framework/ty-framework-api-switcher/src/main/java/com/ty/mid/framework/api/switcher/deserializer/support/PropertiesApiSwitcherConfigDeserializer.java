package com.ty.mid.framework.api.switcher.deserializer.support;

/**
 * 基于属性文件的开关配置 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-14 19:10
 */
public class PropertiesApiSwitcherConfigDeserializer extends AbstractKeyValueApiSwitcherConfigDeserializer {

    public PropertiesApiSwitcherConfigDeserializer() {
    }

    public PropertiesApiSwitcherConfigDeserializer(String fromDateSuffix, String thruDateSuffix, String tipSuffix) {
        super(fromDateSuffix, thruDateSuffix, tipSuffix);
    }

    public PropertiesApiSwitcherConfigDeserializer(String fromDateSuffix, String thruDateSuffix, String tipSuffix, String datePattern) {
        super(fromDateSuffix, thruDateSuffix, tipSuffix, datePattern);
    }

    public PropertiesApiSwitcherConfigDeserializer(String fromDateSuffix, String thruDateSuffix, String tipSuffix, String datePattern, String lineSeparator) {
        super(fromDateSuffix, thruDateSuffix, tipSuffix, datePattern, lineSeparator);
    }
}
