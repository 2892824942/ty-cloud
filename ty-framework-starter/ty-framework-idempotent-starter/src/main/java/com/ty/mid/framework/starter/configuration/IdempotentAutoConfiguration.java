package com.ty.mid.framework.starter.configuration;

import com.ty.mid.framework.autoconfigure.LockAutoConfiguration;
import com.ty.mid.framework.core.config.IdempotentConfiguration;
import com.ty.mid.framework.idempotent.aspect.IdempotentAspect;
import com.ty.mid.framework.idempotent.service.IdempotentService;
import com.ty.mid.framework.idempotent.service.support.RedisIdempotentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;

@Configuration
@ConditionalOnProperty(prefix = "framework.idempotent", value = "enable", matchIfMissing = false)
@Import({IdempotentConfiguration.class, LockAutoConfiguration.class})
@AutoConfigureAfter(LockAutoConfiguration.class)
public class IdempotentAutoConfiguration {

    @Autowired
    private IdempotentConfiguration configuration;

    @Bean
    @ConditionalOnBean({LockRegistry.class, StringRedisTemplate.class})
    @ConditionalOnMissingBean
    IdempotentService idempotentService(LockRegistry lockRegistry, StringRedisTemplate redisTemplate) {
        return new RedisIdempotentService(
                configuration.getLockKeyPrefix(),
                configuration.getLockTimeout(),
                configuration.getLockTimeUnit(),
                configuration.getLockTtlInSeconds(),
                lockRegistry, redisTemplate);
    }

    /**
     * 幂等校验切面
     *
     * @param idempotentService
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    IdempotentAspect idempotentAspect(IdempotentService idempotentService) {
        return new IdempotentAspect(idempotentService);
    }

}
