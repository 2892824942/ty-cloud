package com.ty.mid.framework.autoconfigure;


import com.ty.mid.framework.web.ApplicationRunnerLauncher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Banner 的自动配置类 <p>
 *
 * @author suyoulinag
 */
@ConditionalOnProperty(prefix = "framework.runner", value = "enable", matchIfMissing = true)
@RequiredArgsConstructor
public class RunnerAutoConfiguration {

    @Bean
    public ApplicationRunnerLauncher bannerApplicationRunner() {
        return new ApplicationRunnerLauncher();
    }

}
