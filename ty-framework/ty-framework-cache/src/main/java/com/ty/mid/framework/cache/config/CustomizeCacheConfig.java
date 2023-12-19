package com.ty.mid.framework.cache.config;

import com.ty.mid.framework.cache.config.redisson.LocalCachedMapOptions;
import lombok.Data;

@Data
public class CustomizeCacheConfig {
    LocalCachedMapOptions<Object, Object> localCached;
}
