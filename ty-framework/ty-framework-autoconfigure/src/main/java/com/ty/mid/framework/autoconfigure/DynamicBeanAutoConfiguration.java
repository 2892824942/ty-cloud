package com.ty.mid.framework.autoconfigure;

import cn.hutool.core.collection.CollUtil;
import com.ty.mid.framework.web.config.WebConfig;
import com.ty.mid.framework.web.swagger.SwaggerUtil;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.util.Map;
@RequiredArgsConstructor
public class DynamicBeanAutoConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    private final WebConfig webConfig;


    private final ConfigurableApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, WebConfig.Api> customApiMap = webConfig.getCustomApi();
        if (CollUtil.isEmpty(customApiMap)) {
            return;
        }

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        customApiMap.forEach((k, v) -> {
            boolean b = applicationContext.containsBean(k + "GroupedOpenApi");
            if (!b) {
                GroupedOpenApi groupedOpenApi = SwaggerUtil.buildGroupedOpenApi(k, v.getPrefix());
                beanFactory.registerSingleton(k + "GroupedOpenApi", groupedOpenApi);
            }
        });
    }


}
