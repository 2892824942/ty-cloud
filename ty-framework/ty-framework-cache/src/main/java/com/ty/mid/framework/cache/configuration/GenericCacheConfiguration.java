/* <p>
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

package com.ty.mid.framework.cache.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.Collection;

/**
 * Generic cache configuration based on arbitrary {@link Cache} instances defined in the <p>
 * context. <p>
 *
 * @author Stephane Nicoll
 */
@ConditionalOnBean(Cache.class)
@AutoConfigureBefore({CacheAutoConfiguration.class})
public class GenericCacheConfiguration {

    @Bean
    SimpleCacheManager simpleCacheManager(CacheManagerCustomizers customizers, Collection<Cache> caches) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return customizers.customize(cacheManager);
    }

}
