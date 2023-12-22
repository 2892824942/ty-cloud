package com.ty.mid.framework.application.runner.config;


import com.ty.mid.framework.application.runner.core.ApplicationRunnerLauncher;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Banner 的自动配置类
 *
 * @author suyoulinag
 */
@ConditionalOnProperty(prefix = "framework.runner", value = "enable", matchIfMissing = true)
public class RunnerAutoConfiguration {
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    SpringContextHelper springContextHelper() {
        SpringContextHelper helper = new SpringContextHelper();
        helper.setApplicationContext(applicationContext);
        return helper;
    }

    @Bean
    public ApplicationRunnerLauncher bannerApplicationRunner() {
        return new ApplicationRunnerLauncher();
    }

}
