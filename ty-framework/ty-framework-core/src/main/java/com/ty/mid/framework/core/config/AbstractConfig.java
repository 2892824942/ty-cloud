package com.ty.mid.framework.core.config;

import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
public abstract class AbstractConfig {
    public static final String FRAMEWORK_PREFIX = "framework.";

}
