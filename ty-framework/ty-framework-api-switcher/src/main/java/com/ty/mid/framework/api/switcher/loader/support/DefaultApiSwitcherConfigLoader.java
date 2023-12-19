package com.ty.mid.framework.api.switcher.loader.support;

import com.ty.mid.framework.api.switcher.deserializer.ApiSwitcherConfigDeserializer;

/**
 * 默认 API 开关配置加载类
 */
public class DefaultApiSwitcherConfigLoader extends AbstractApiSwitcherConfigLoader {

    public DefaultApiSwitcherConfigLoader(ApiSwitcherConfigDeserializer deserializer, String initialConfig) {
        super(deserializer, initialConfig);
    }
}
