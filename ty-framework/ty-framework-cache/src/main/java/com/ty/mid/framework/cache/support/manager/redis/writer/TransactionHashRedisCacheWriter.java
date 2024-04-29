package com.ty.mid.framework.cache.support.manager.redis.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.cache.CacheStatistics;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@link RedisCacheWriter} implementation capable of reading/writing binary data from/to Redis in {@literal standalone} <p>
 * and {@literal cluster} environments. Works upon a given {@link RedisConnectionFactory} to obtain the actual <p>
 * {@link RedisConnection}. <br /> <p>
 * {@link TransactionHashRedisCacheWriter} can be used in <p>
 * {@link RedisCacheWriter#lockingRedisCacheWriter(RedisConnectionFactory) locking} or <p>
 * {@link RedisCacheWriter#nonLockingRedisCacheWriter(RedisConnectionFactory) non-locking} mode. While <p>
 * {@literal non-locking} aims for maximum performance it may result in overlapping, non atomic, command execution for <p>
 * operations spanning multiple Redis interactions like {@code putIfAbsent}. The {@literal locking} counterpart prevents <p>
 * command overlap by setting an explicit lock key and checking against presence of this key which leads to additional <p>
 * requests and potential command wait times. <p>
 * @author Christoph Strobl <p>
 * @author Mark Paluch <p>
 * @since 2.0 
 */
@Slf4j
public class TransactionHashRedisCacheWriter implements RedisCacheWriter {

    private static final byte[] BINARY_NULL_VALUE = RedisSerializer.java().serialize(NullValue.INSTANCE);
    private static final Duration DEFAULT_DURATION = Duration.of(5, ChronoUnit.MINUTES);
    private final RedisConnectionFactory connectionFactory;
    private final CacheStatisticsCollector statistics;
    private Duration nullValueTimeToLive = Duration.ZERO;
    private boolean allowNullValue = Boolean.FALSE;


    /**
     * @param connectionFactory must not be {@literal null}.
     */
    public TransactionHashRedisCacheWriter(RedisConnectionFactory connectionFactory) {
        this(connectionFactory, DEFAULT_DURATION, Boolean.FALSE);
    }

    public TransactionHashRedisCacheWriter(RedisConnectionFactory connectionFactory, Duration nullValueTimeToLive, boolean allowNullValue) {
        this(connectionFactory, nullValueTimeToLive, allowNullValue, CacheStatisticsCollector.none());
    }

    public TransactionHashRedisCacheWriter(RedisConnectionFactory connectionFactory, Duration nullValueTimeToLive, boolean allowNullValue, CacheStatisticsCollector statistics) {
        this.connectionFactory = connectionFactory;
        this.allowNullValue = allowNullValue;
        if (Objects.nonNull(nullValueTimeToLive)) {
            this.nullValueTimeToLive = nullValueTimeToLive;
        }
        this.statistics = statistics;
    }

    private static boolean shouldExpireWithin(@Nullable Duration ttl) {
        return ttl != null && !ttl.isZero() && !ttl.isNegative();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.cache.RedisCacheWriter#put(java.lang.String, byte[], byte[], java.time.Duration)
     */
    //TODO 增加数据本地版本控制，减少redis发送数据量
    @Override
    public void put(String name, byte[] key, byte[] value, @Nullable Duration ttl) {

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(value, "Value must not be null!");

        execute(name, connection -> {
            connection.hSet(name.getBytes(), key, value);
            setExpire(name, key, value, ttl, connection);
            return "OK";
        });
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.cache.RedisCacheWriter#get(java.lang.String, byte[])
     */
    @Override
    public byte[] get(String name, byte[] key) {

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");

        return execute(name, connection -> connection.hGet(name.getBytes(), key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.cache.RedisCacheWriter#putIfAbsent(java.lang.String, byte[], byte[], java.time.Duration)
     */
    @Override
    public byte[] putIfAbsent(String name, byte[] key, byte[] value, @Nullable Duration ttl) {

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(value, "Value must not be null!");
        execute(name, connection -> {
            //nullValue时间特殊设置
            Boolean result = connection.hSetNX(name.getBytes(), key, value);
            setExpire(name, key, value, ttl, connection);

            return "OK";
        });
        return null;
    }


    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.cache.RedisCacheWriter#remove(java.lang.String, byte[])
     */
    @Override
    public void remove(String name, byte[] key) {

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(key, "Key must not be null!");
        log.info("now remove cacheName:{},key:{}", name, new String(key));

        execute(name, connection -> connection.hDel(name.getBytes(), key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.cache.RedisCacheWriter#clean(java.lang.String, byte[])
     */
    @Override
    public void clean(String name, byte[] pattern) {

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(pattern, "Pattern must not be null!");

        execute(name, connection -> {

            byte[][] keys = Optional.ofNullable(connection.keys(pattern)).orElse(Collections.emptySet())
                    .toArray(new byte[0][]);

            if (keys.length > 0) {
                connection.hDel(name.getBytes(), keys);
            }
            return "OK";
        });
    }

    @Override
    public void clearStatistics(String name) {
        statistics.reset(name);
    }

    @Override
    public RedisCacheWriter withStatisticsCollector(CacheStatisticsCollector cacheStatisticsCollector) {
        return new TransactionHashRedisCacheWriter(connectionFactory, nullValueTimeToLive, allowNullValue, cacheStatisticsCollector);
    }

    private void setExpire(String name, byte[] key, byte[] value, @Nullable Duration ttl, RedisConnection connection) {
        if (Arrays.equals(BINARY_NULL_VALUE, value) && !Objects.equals(Duration.ZERO, nullValueTimeToLive)) {
            Duration realNullValueTimeToLive = allowNullValue ? nullValueTimeToLive : DEFAULT_DURATION;
            connection.expire(name.getBytes(), Expiration.from(realNullValueTimeToLive).getExpirationTimeInSeconds());
        } else if (shouldExpireWithin(ttl)) {
            connection.expire(name.getBytes(), Expiration.from(ttl).getExpirationTimeInSeconds());
        }
    }

    /**
     * @return {@literal true} if {@link RedisCacheWriter} uses locks.
     */

    private <T> T execute(String name, Function<RedisConnection, T> callback) {

        RedisConnection connection = RedisConnectionUtils.bindConnection(connectionFactory);
        try {
            return callback.apply(connection);
        } finally {
            connection.close();
        }
    }

    @Override
    public CacheStatistics getCacheStatistics(String cacheName) {
        return statistics.getCacheStatistics(cacheName);
    }
}
