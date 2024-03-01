package com.ty.mid.framework.cache.support.cache;

import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

public class GetIfAbsentRedisCache extends RedisCache {
    private static final byte[] BINARY_NULL_VALUE = RedisSerializer.java().serialize(NullValue.INSTANCE);

    /**
     * Create new {@link RedisCache}.
     *
     * @param name        must not be {@literal null}.
     * @param cacheWriter must not be {@literal null}.
     * @param cacheConfig must not be {@literal null}.
     */
    public GetIfAbsentRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
    }

    private static <T> T valueFromLoader(Object key, Callable<T> valueLoader) {

        try {
            return valueLoader.call();
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper result = get(key);

        if (result != null) {
            return (T) result.get();
        }

        T value = valueFromLoader(key, valueLoader);
        putIfAbsent(key, value);
        return value;
    }

    /**
     * 底层处理第二类丢失更新机制依赖nullValue，这里允许底层框架设置的nullValue解析，但不会干涉外部用户的nullAble设置
     *
     * @param storeValue
     * @return
     */
    @Nullable
    @Override
    protected Object fromStoreValue(@Nullable Object storeValue) {
        if (storeValue == NullValue.INSTANCE) {
            return null;
        }
        return storeValue;
    }
}
