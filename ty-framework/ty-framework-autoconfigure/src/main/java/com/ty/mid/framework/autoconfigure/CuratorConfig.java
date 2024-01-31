package com.ty.mid.framework.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = CuratorConfig.PREFIX)
@Data
public class CuratorConfig {
    public static final String PREFIX = "application.zookeeper";

    private String address;
    private int connectionTimeout;
    private int sessionTimeout;
    private int sleepTimeOut;
    private int maxRetries;
    private String namespace;

}
