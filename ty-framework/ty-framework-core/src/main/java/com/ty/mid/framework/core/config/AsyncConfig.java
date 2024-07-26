package com.ty.mid.framework.core.config;


import com.ty.mid.framework.common.constant.FrameworkConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import static com.ty.mid.framework.core.config.AsyncConfig.PREFIX;

@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(PREFIX)
@Validated
@Data
public class AsyncConfig extends AbstractConfig{
    public static final String PREFIX = FRAMEWORK_PREFIX + "async";

    private int corePoolSize = FrameworkConstant.Async.corePoolSize;

    private int maxPoolSize = FrameworkConstant.Async.maxPoolSize;

    private int queueCapacity = FrameworkConstant.Async.queueCapacity;

    private String threadNamePrefix = "framework-async-";

}
