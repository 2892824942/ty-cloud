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
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Simplest cache configuration, usually used as a fallback. <p>
 * @author Stephane Nicoll 
 */
@AutoConfigureBefore({CacheAutoConfiguration.class})
@ConditionalOnMissingBean(CacheManager.class)
public class SimpleCacheConfiguration {

    @Bean
    ConcurrentMapCacheManager concurrentMapCacheManager(CacheProperties cacheProperties,
                                                        CacheManagerCustomizers cacheManagerCustomizers) {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            cacheManager.setCacheNames(cacheNames);
        }
        return cacheManagerCustomizers.customize(cacheManager);
    }

}
