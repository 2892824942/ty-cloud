package com.ty.mid.framework.api.switcher.deserializer;

import com.ty.mid.framework.api.switcher.exception.ApiSwitcherConfigDeserializeException;
import com.ty.mid.framework.api.switcher.model.ApiSwitcherConfig;

import java.util.Map;

/**
 * api 开关配置反序列化 <p>
 * 用于将行配置转为 ApiSwitcherConfig <p>
 * @author suyouliang <p>
 * @createTime 2023-08-14 18:18 
 */
public interface ApiSwitcherConfigDeserializer {

    Map<String, ApiSwitcherConfig> deserialize(String configString) throws ApiSwitcherConfigDeserializeException;

}
