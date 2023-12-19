package com.ty.mid.framework.core.config;


import com.ty.mid.framework.common.constant.FrameworkConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appliction.async")
@Data
public class AsyncConfiguration {

    private int corePoolSize = FrameworkConstant.Async.corePoolSize;

    private int maxPoolSize = FrameworkConstant.Async.maxPoolSize;

    private int queueCapacity = FrameworkConstant.Async.queueCapacity;

    private String threadNamePrefix = "framework-async-";

}
