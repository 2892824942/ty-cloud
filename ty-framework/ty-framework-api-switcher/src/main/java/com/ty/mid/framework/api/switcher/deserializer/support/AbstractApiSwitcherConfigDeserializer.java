package com.ty.mid.framework.api.switcher.deserializer.support;

import com.ty.mid.framework.api.switcher.deserializer.ApiSwitcherConfigDeserializer;
import com.ty.mid.framework.api.switcher.exception.ApiSwitcherConfigDeserializeException;
import com.ty.mid.framework.api.switcher.model.ApiSwitcherConfig;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 抽象开关配置反序列化类 <p>
 *
 * @author suyouliang <p>
 * @createTime 2023-08-14 18:21
 */
public abstract class AbstractApiSwitcherConfigDeserializer implements ApiSwitcherConfigDeserializer {


    public Map<String, ApiSwitcherConfig> deserialize(String configString) throws ApiSwitcherConfigDeserializeException {
        if (StringUtils.isEmpty(configString)) {
            throw new ApiSwitcherConfigDeserializeException("can not deserialize because config string is null!");
        }

        return this.doDeserialize(configString);
    }

    protected abstract Map<String, ApiSwitcherConfig> doDeserialize(String configString);

}
