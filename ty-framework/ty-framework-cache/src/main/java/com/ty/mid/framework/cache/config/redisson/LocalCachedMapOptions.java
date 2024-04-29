package com.ty.mid.framework.cache.config.redisson;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.redisson.api.MapOptions;
import org.redisson.api.map.MapLoader;
import org.redisson.api.map.MapWriter;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class LocalCachedMapOptions<K, V> extends MapOptions<K, V> {


    private ReconnectionStrategy reconnectionStrategy;
    private SyncStrategy syncStrategy;
    private EvictionPolicy evictionPolicy;

    ;
    private int cacheSize;
    private long timeToLiveInMillis;
    private long maxIdleInMillis;
    private CacheProvider cacheProvider;
    private StoreMode storeMode;

    protected LocalCachedMapOptions() {
    }

    /**
     * Creates a new instance of LocalCachedMapOptions with default options.
     *
     * This is equivalent to:
     * <pre>
     *     new LocalCachedMapOptions()
     *      .cacheSize(0).timeToLive(0).maxIdle(0)
     *      .evictionPolicy(EvictionPolicy.NONE)
     *      .reconnectionStrategy(ReconnectionStrategy.NONE)
     *      .cacheProvider(CacheProvider.REDISSON)
     *      .syncStrategy(SyncStrategy.INVALIDATE);
     * </pre>
     *
     * @param <K> key type
     * @param <V> value type
     * @return LocalCachedMapOptions instance
     */
    public static <K, V> LocalCachedMapOptions<K, V> defaults() {
        return new LocalCachedMapOptions<K, V>()
                //淘汰机制有LFU, LRU和NONE这几种算法策略可供选择
                .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU)
                //当本地map与redis失联后的策略，这里选择清空本地缓存，适合对一致性比较苛刻的场景
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR)
                //当本地map更改时，将更改的map句柄广播给所有的实例，实例删除本地缓存，触发重新拉取。UPDATE则为广播完整数据（大批量修改场景支持不好）
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE)
                //本地缓存使用caffeine实现，也可使用Redisson自己的实现
                .cacheProvider(LocalCachedMapOptions.CacheProvider.REDISSON)
                //存储模式，redis和本地都存。另一个模式是LOCALCACHE，此时就是纯本地缓存
                .storeMode(LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS)
                .cacheSize(1000)
                // 每个Map本地缓存里元素的有效时间，默认毫秒为单位
                .timeToLive(5, TimeUnit.MINUTES)
                // 每个Map本地缓存里元素的最长闲置时间，默认毫秒为单位
                .maxIdle(2, TimeUnit.MINUTES);
    }

    public CacheProvider getCacheProvider() {
        return cacheProvider;
    }

    public EvictionPolicy getEvictionPolicy() {
        return evictionPolicy;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public long getTimeToLiveInMillis() {
        return timeToLiveInMillis;
    }

    public long getMaxIdleInMillis() {
        return maxIdleInMillis;
    }

    /**
     * Defines local cache size. If size is <code>0</code> then local cache is unbounded.
     *
     * @param cacheSize - size of cache
     * @return LocalCachedMapOptions instance
     */
    public LocalCachedMapOptions<K, V> cacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
        return this;
    }

    public ReconnectionStrategy getReconnectionStrategy() {
        return reconnectionStrategy;
    }

    public SyncStrategy getSyncStrategy() {
        return syncStrategy;
    }

    public LocalCachedMapOptions<K, V> reconnectionStrategy(ReconnectionStrategy reconnectionStrategy) {
        if (reconnectionStrategy == null) {
            throw new NullPointerException("reconnectionStrategy can't be null");
        }

        this.reconnectionStrategy = reconnectionStrategy;
        return this;
    }

    public LocalCachedMapOptions<K, V> syncStrategy(SyncStrategy syncStrategy) {
        if (syncStrategy == null) {
            throw new NullPointerException("syncStrategy can't be null");
        }

        this.syncStrategy = syncStrategy;
        return this;
    }

    /**
     * Defines local cache eviction policy.
     *
     * @param evictionPolicy <p><code>LRU</code> - uses local cache with LRU (least recently used) eviction policy.
     *                       <p><code>LFU</code> - uses local cache with LFU (least frequently used) eviction policy.
     *                       <p><code>SOFT</code> - uses local cache with soft references. The garbage collector will evict items from the local cache when the JVM is running out of memory.
     *                       <p><code>WEAK</code> - uses local cache with weak references. The garbage collector will evict items from the local cache when it became weakly reachable.
     *                       <p><code>NONE</code> - doesn't use eviction policy, but timeToLive and maxIdleTime params are still working.
     * @return LocalCachedMapOptions instance
     */
    public LocalCachedMapOptions<K, V> evictionPolicy(EvictionPolicy evictionPolicy) {
        if (evictionPolicy == null) {
            throw new NullPointerException("evictionPolicy can't be null");
        }
        this.evictionPolicy = evictionPolicy;
        return this;
    }

    /**
     * Defines time to live in milliseconds of each map entry in local cache.
     * If value equals to <code>0</code> then timeout is not applied
     *
     * @param timeToLiveInMillis - time to live in milliseconds
     * @return LocalCachedMapOptions instance
     */
    public LocalCachedMapOptions<K, V> timeToLive(long timeToLiveInMillis) {
        this.timeToLiveInMillis = timeToLiveInMillis;
        return this;
    }

    /**
     * Defines time to live of each map entry in local cache.
     * If value equals to <code>0</code> then timeout is not applied
     *
     * @param timeToLive - time to live
     * @param timeUnit   - time unit
     * @return LocalCachedMapOptions instance
     */
    public LocalCachedMapOptions<K, V> timeToLive(long timeToLive, TimeUnit timeUnit) {
        return timeToLive(timeUnit.toMillis(timeToLive));
    }

    /**
     * Defines max idle time in milliseconds of each map entry in local cache.
     * If value equals to <code>0</code> then timeout is not applied
     *
     * @param maxIdleInMillis - time to live in milliseconds
     * @return LocalCachedMapOptions instance
     */
    public LocalCachedMapOptions<K, V> maxIdle(long maxIdleInMillis) {
        this.maxIdleInMillis = maxIdleInMillis;
        return this;
    }

    /**
     * Defines max idle time of each map entry in local cache.
     * If value equals to <code>0</code> then timeout is not applied
     *
     * @param maxIdle  - max idle time
     * @param timeUnit - time unit
     * @return LocalCachedMapOptions instance
     */
    public LocalCachedMapOptions<K, V> maxIdle(long maxIdle, TimeUnit timeUnit) {
        return maxIdle(timeUnit.toMillis(maxIdle));
    }

    public StoreMode getStoreMode() {
        return storeMode;
    }

    /**
     * Defines store mode of cache data.
     *
     * @param storeMode <p><code>LOCALCACHE</code> - store data in local cache only.
     *                  <p><code>LOCALCACHE_REDIS</code> - store data in both Redis and local cache.
     * @return LocalCachedMapOptions instance
     */
    public LocalCachedMapOptions<K, V> storeMode(StoreMode storeMode) {
        this.storeMode = storeMode;
        return this;
    }

    /**
     * Defines Cache provider used as local cache store.
     *
     * @param cacheProvider <p><code>REDISSON</code> - uses Redisson own implementation.
     *                      <p><code>CAFFEINE</code> - uses Caffeine implementation.
     * @return LocalCachedMapOptions instance
     */
    public LocalCachedMapOptions<K, V> cacheProvider(CacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
        return this;
    }

    @Override
    public LocalCachedMapOptions<K, V> writeBehindBatchSize(int writeBehindBatchSize) {
        return (LocalCachedMapOptions<K, V>) super.writeBehindBatchSize(writeBehindBatchSize);
    }

    @Override
    public LocalCachedMapOptions<K, V> writeBehindDelay(int writeBehindDelay) {
        return (LocalCachedMapOptions<K, V>) super.writeBehindDelay(writeBehindDelay);
    }

    @Override
    public LocalCachedMapOptions<K, V> writer(MapWriter<K, V> writer) {
        return (LocalCachedMapOptions<K, V>) super.writer(writer);
    }

    @Override
    public LocalCachedMapOptions<K, V> writeMode(org.redisson.api.MapOptions.WriteMode writeMode) {
        return (LocalCachedMapOptions<K, V>) super.writeMode(writeMode);
    }

    @Override
    public LocalCachedMapOptions<K, V> loader(MapLoader<K, V> loader) {
        return (LocalCachedMapOptions<K, V>) super.loader(loader);
    }

    /**
     * Various strategies to avoid stale objects in local cache.
     * Handle cases when map instance has been disconnected for a while.
     */
    public enum ReconnectionStrategy {

        /**
         * No reconnect handling.
         */
        NONE,

        /**
         * Clear local cache if map instance disconnected.
         */
        CLEAR,

        /**
         * Store invalidated entry hash in invalidation log for 10 minutes.
         * Cache keys for stored invalidated entry hashes will be removed
         * if LocalCachedMap instance has been disconnected less than 10 minutes
         * or whole local cache will be cleaned otherwise.
         */
        LOAD

    }

    public enum SyncStrategy {

        /**
         * No synchronizations on map changes.
         */
        NONE,

        /**
         * Invalidate local cache entry across all LocalCachedMap instances on map entry change. Broadcasts map entry hash (16 bytes) to all instances.
         */
        INVALIDATE,

        /**
         * Update local cache entry across all LocalCachedMap instances on map entry change. Broadcasts full map entry state (Key and Value objects) to all instances.
         */
        UPDATE

    }

    public enum EvictionPolicy {

        /**
         * Local cache without eviction.
         */
        NONE,

        /**
         * Least Recently Used local cache.
         */
        LRU,

        /**
         * Least Frequently Used local cache.
         */
        LFU,

        /**
         * Local cache with Soft Reference used for values.
         * All references will be collected by GC
         */
        SOFT,

        /**
         * Local cache with Weak Reference used for values.
         * All references will be collected by GC
         */
        WEAK
    }

    public enum CacheProvider {

        REDISSON,

        CAFFEINE

    }

    public enum StoreMode {

        /**
         * Store data only in local cache.
         */
        LOCALCACHE,

        /**
         * Store data only in both Redis and local cache.
         */
        LOCALCACHE_REDIS

    }


}