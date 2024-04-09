package com.ty.mid.framework.api.switcher.loader.support;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.api.switcher.deserializer.ApiSwitcherConfigDeserializer;
import com.ty.mid.framework.api.switcher.exception.ApiSwitcherConfigDeserializeException;
import com.ty.mid.framework.api.switcher.loader.ApiSwitcherConfigLoader;
import com.ty.mid.framework.api.switcher.model.ApiSwitcherConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AbstractApiSwitcherConfigLoader implements ApiSwitcherConfigLoader {

    private ApiSwitcherConfigDeserializer deserializer;

    private Map<String, ApiSwitcherConfig> aipSwitcherConfig = new HashMap<>();

    public AbstractApiSwitcherConfigLoader(ApiSwitcherConfigDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    public AbstractApiSwitcherConfigLoader(ApiSwitcherConfigDeserializer deserializer, String initialConfig) {
        this.deserializer = deserializer;
        if (!StrUtil.isEmpty(initialConfig)) {
            this.reloadConfig(initialConfig);
        }
    }

    @Override
    public ApiSwitcherConfig loadApiSwitcherConfig(String apiName) {
        return this.aipSwitcherConfig.get(apiName);
    }


    protected void reloadConfig(String configString) {
        log.info("start reloading api switcher config, current config size: {}", this.aipSwitcherConfig.size());
        try {
            this.aipSwitcherConfig = this.deserializer.deserialize(configString);
            log.info("reload api switcher config success, now config size: {}", this.aipSwitcherConfig.size());
        } catch (ApiSwitcherConfigDeserializeException e) {
            log.warn("reload config error, error message: {}", e.getMessage());
        }
    }
}
