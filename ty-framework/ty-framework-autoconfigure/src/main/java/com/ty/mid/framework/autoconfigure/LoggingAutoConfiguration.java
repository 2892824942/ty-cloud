//package com.ty.mid.framework.autoconfigure;
//
//import com.ty.mid.framework.core.config.LoggingConfiguration;
//import com.ty.mid.framework.core.expression.ExpressionManager;
//import com.ty.mid.framework.core.expression.support.DefaultExpressionManager;
//import com.ty.mid.framework.logging.aspect.LoggingAspect;
//import com.ty.mid.framework.logging.core.register.LogRegistry;
//import com.ty.mid.framework.logging.formatter.MethodArgumentsFormatter;
//import com.ty.mid.framework.logging.formatter.support.DefaultMethodArgumentsFormatter;
//import com.ty.mid.framework.logging.initializer.LoggingInitializer;
//import com.ty.mid.framework.logging.model.LoggingEvent;
//import com.ty.mid.framework.logging.persist.LoggingEventPersistenceService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.env.Environment;
//
//@ConditionalOnClass(LoggingEventPersistenceService.class)
//public class LoggingAutoConfiguration {
//
//    @Autowired
//    private LoggingConfiguration configuration;
//
//    @Bean
//    @ConditionalOnMissingBean
//    LoggingEventPersistenceService logEventPersistenceService() {
//        return new LoggingEventPersistenceService() {
//
//            private final Logger log = LoggerFactory.getLogger("Non-Persist Log service");
//
//            @Override
//            public <T extends LoggingEvent> void persist(T logEvent) {
//                log.warn("log persistence service [{}] not implemented", LoggingEventPersistenceService.class.getName());
//            }
//        };
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    MethodArgumentsFormatter loggingMethodArgumentsFormatter() {
//        return DefaultMethodArgumentsFormatter.INSTANCE;
//    }
//
//
//    @Bean
//    @ConditionalOnMissingBean
//    ExpressionManager expressionManager() {
//        return DefaultExpressionManager.INSTANCE;
//    }
//
//
//    @Bean
//    @ConditionalOnMissingBean
//    LoggingAspect loggingAspect(Environment env, LoggingEventPersistenceService loggingEventPersistenceService, MethodArgumentsFormatter methodArgumentsFormatter, ExpressionManager expressionManager) {
//        return new LoggingAspect(env, loggingEventPersistenceService, methodArgumentsFormatter, expressionManager);
//    }
//
//    @Bean
//    @ConditionalOnProperty(prefix = "framework.logging", value = "enabled", matchIfMissing = false)
//    LoggingInitializer loggingInitializer(@Value("${spring.application.name}") String appName,
//                                          @Value("${server.port}") String serverPort,
//                                          LoggingConfiguration configuration) {
//        LogRegistry.register();
//        return new LoggingInitializer(appName, serverPort, configuration);
//    }
//
//}
