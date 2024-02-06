package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.core.config.AbstractConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = CuratorConfig.PREFIX)
@Data
public class CuratorConfig extends AbstractConfig {
    public static final String PREFIX = FRAMEWORK_PREFIX + "zookeeper";

    private String address;
    private int connectionTimeout;
    private int sessionTimeout;
    private int sleepTimeOut;
    private int maxRetries;
    private String namespace;

}
