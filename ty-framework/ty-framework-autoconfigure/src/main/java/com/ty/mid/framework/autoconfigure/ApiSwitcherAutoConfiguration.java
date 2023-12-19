package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.api.switcher.aspect.ApiSwitcherAspect;
import com.ty.mid.framework.api.switcher.deserializer.ApiSwitcherConfigDeserializer;
import com.ty.mid.framework.api.switcher.deserializer.support.TextApiSwitcherConfigDeserializer;
import com.ty.mid.framework.api.switcher.loader.ApiSwitcherConfigLoader;
import com.ty.mid.framework.api.switcher.loader.support.DefaultApiSwitcherConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(prefix = "framework.switcher", value = "enable", matchIfMissing = false)
@Slf4j
public class ApiSwitcherAutoConfiguration {

//    @Autowired
//    private ApiSwitcherConfiguration configuration;

    @Bean
    @ConditionalOnMissingBean
    ApiSwitcherConfigDeserializer apiSwitcherConfigDeserializer() {
        log.info("configuring [ApiSwitcherConfigDeserializer] with cn.techwolf.ugroup.framework.api.switcher.deserializer.support.TextApiSwitcherConfigDeserializer");
        return new TextApiSwitcherConfigDeserializer();
    }

    @Bean
    @ConditionalOnMissingBean
    ApiSwitcherConfigLoader apiSwitcherConfigLoader(ApiSwitcherConfigDeserializer deserializer) {
        log.info("configuring [ApiSwitcherConfigLoader] with DefaultApiSwitcherConfigLoader");
        return new DefaultApiSwitcherConfigLoader(apiSwitcherConfigDeserializer(), "");
    }

    /**
     * api 开关切面
     *
     * @param apiSwitcherConfigLoader
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    ApiSwitcherAspect apiSwitcherAspect(ApiSwitcherConfigLoader apiSwitcherConfigLoader) {
        log.info("configuring ApiSwitcherAspect");
        return new ApiSwitcherAspect(apiSwitcherConfigLoader);
    }

}
