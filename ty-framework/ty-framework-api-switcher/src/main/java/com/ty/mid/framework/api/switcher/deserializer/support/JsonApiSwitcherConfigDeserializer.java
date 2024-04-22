package com.ty.mid.framework.api.switcher.deserializer.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.ty.mid.framework.api.switcher.exception.ApiSwitcherConfigDeserializeException;
import com.ty.mid.framework.api.switcher.model.ApiSwitcherConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于json的开关配置
 *
 * @author suyouliang
 * @createTime 2023-08-14 19:10
 */
public class JsonApiSwitcherConfigDeserializer extends AbstractApiSwitcherConfigDeserializer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ObjectMapper objectMapper;

    private MapType mapType;

    public JsonApiSwitcherConfigDeserializer() {
        this.objectMapper = new ObjectMapper();
        mapType = this.objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, ApiSwitcherConfig.class);
    }

    public JsonApiSwitcherConfigDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        mapType = this.objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, ApiSwitcherConfig.class);
    }

    @Override
    protected Map<String, ApiSwitcherConfig> doDeserialize(String configString) {
        try {
            Map<String, ApiSwitcherConfig> ret = this.objectMapper.readValue(configString, mapType);
            return ret;
        } catch (IOException e) {
            log.warn("deserialize json error", e);
            throw new ApiSwitcherConfigDeserializeException("deserialize json error");
        }
    }
}
