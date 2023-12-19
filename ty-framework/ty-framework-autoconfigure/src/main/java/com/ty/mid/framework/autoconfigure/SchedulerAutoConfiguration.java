package com.ty.mid.framework.autoconfigure;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync(proxyTargetClass = true)
@EnableScheduling
public class SchedulerAutoConfiguration {


}
