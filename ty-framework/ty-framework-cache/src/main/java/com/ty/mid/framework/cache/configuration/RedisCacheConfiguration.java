package com.ty.mid.framework.cache.configuration;/* <p>
 * Copyright 2012-2023 the original author or authors. <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <p>
 * you may not use this file except in compliance with the License. <p>
 * You may obtain a copy of the License at <p>
 *      https://www.apache.org/licenses/LICENSE-2.0 <p>
 * Unless required by applicable law or agreed to in writing, software <p>
 * distributed under the License is distributed on an "AS IS" BASIS, <p>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <p>
 * See the License for the specific language governing permissions and <p>
 * limitations under the License.
 */

import com.ty.mid.framework.cache.condition.CachePlusCondition;
import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.cache.configuration.base.AbstractRedisCacheConfiguration;
import com.ty.mid.framework.cache.constant.CachePlusType;
import com.ty.mid.framework.cache.support.manager.redis.writer.HashRedisCacheWriter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Objects;

/**
 * Redis cache configuration. <p>
 *
 * @author Stephane Nicoll <p>
 * @author Mark Paluch <p>
 * @author Ryon Day
 */
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(RedisConnectionFactory.class)
@EnableConfigurationProperties(CachePlusConfig.class)
@Conditional(CachePlusCondition.class)
public class RedisCacheConfiguration extends AbstractRedisCacheConfiguration {

    public RedisCacheConfiguration(CachePlusConfig cachePlusConfig) {
        super(cachePlusConfig);
    }

    @Bean
    RedisCacheManager redisCacheManager(CacheManagerCustomizers cacheManagerCustomizers,
                                        ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
                                        ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                                        RedisConnectionFactory redisConnectionFactory, ResourceLoader resourceLoader) {
        RedisCacheManagerBuilder builder = handleBuilder(CachePlusType.REDIS, redisCacheConfiguration, redisConnectionFactory, resourceLoader);
        if (Objects.equals(CachePlusConfig.Redis.StoreType.HASH, cachePlusConfig.getRedis().getStoreType())) {
            builder.cacheWriter(new HashRedisCacheWriter(redisConnectionFactory, BatchStrategies.keys()));
        }
        redisCacheManagerBuilderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return cacheManagerCustomizers.customize(builder.build());
    }


}
