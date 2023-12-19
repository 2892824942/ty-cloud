package com.ty.mid.framework.core.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@NoArgsConstructor
@Slf4j
public class ExpireCacheInMemory<K, V> {

    /**
     * 时间一分钟
     */
    private static final Long ONE_MINUTE = 60 * 1000L;
    /**
     * 缓存最大个数
     */
    private static final Integer CACHE_MAX_NUMBER = 1000;
    /**
     * 清理过期缓存是否在运行
     */
    private static final BlockingQueue<String> BLOCKING_QUEUE = new LinkedBlockingDeque<>();
    /**
     * 清理缓存的标记
     */
    private static final String QUEUE_FLAG = "run";
    /**
     * 缓存对象
     */
    private final Map<K, CacheObj<V>> cacheObjectMap = new ConcurrentHashMap<>();
    /**
     * 这个记录了缓存使用的最后一次的记录，最近使用的在最前面K
     */
    private final List<K> cacheUseLogList = new LinkedList<>();
    /**
     * 当前缓存个数
     */
    private AtomicInteger currentSize = new AtomicInteger(0);
    /**
     * 清理过期缓存是否在运行
     */
    private volatile Boolean cleanThreadIsRun = true;
    /**
     * 清理最近最少使用的缓存（当超过限定的最大缓存值时）
     * 默认不开启
     */
    private volatile Boolean cleanLRU = false;

    /**
     * 开启清理过期缓存的线程
     * //TODO 是否需要支持自动清理
     */ {
        if (cleanThreadIsRun) {
            Runnable cleanTimeOutThread = () -> {
                while (true) {
                    log.debug("clean thread running");
                    deleteTimeOut();
                    if (cleanLRU && currentSize.intValue() >= CACHE_MAX_NUMBER) {
                        deleteLRU();
                    }
                    try {
                        BLOCKING_QUEUE.take();
                    } catch (InterruptedException e) {
                        log.warn("queue take error:{}", e.getMessage());
                    }
                }
            };
            Thread thread = new Thread(cleanTimeOutThread);
            //设置为后台守护线程
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void setCleanLRU(Boolean cleanLRU) {
        this.cleanLRU = cleanLRU;
    }

    public int getCacheSize() {
        return currentSize.intValue();
    }

    /**
     * 设置缓存
     */
    public void asyncClearCache() {
        //如果队列满了，这里会返回false，无需关心队列满的情况
        BLOCKING_QUEUE.offer(QUEUE_FLAG);
    }


    /**
     * 设置缓存,自带computeIfAbsent能力
     */
    public V setCacheComputeIfAbsent(K cacheKey, V cacheValue) {
        return setCacheComputeIfAbsent(cacheKey, cacheValue, -1L);
    }

    /**
     * 设置缓存,自带computeIfAbsent能力
     */
    public V setCacheComputeIfAbsent(K cacheKey, V cacheValue, TimeUnit timeUnit, long duration) {
        long durationMillis = duration == -1L ? -1L : TimeUnit.MILLISECONDS.convert(duration, timeUnit);
        return setCacheComputeIfAbsent(cacheKey, cacheValue, durationMillis);
    }

    /**
     * 设置缓存,自带computeIfAbsent能力
     */
    public V setCacheComputeIfAbsent(K cacheKey, V cacheValue, long duration) {
        return setCacheWithAbsent(cacheKey, cacheValue, duration, true);
    }


    /**
     * 设置缓存，强制替换
     */
    public V setCache(K cacheKey, V cacheValue) {
        return setCache(cacheKey, cacheValue, -1L);
    }


    /**
     * 设置缓存，强制替换
     */
    public V setCache(K cacheKey, V cacheValue, TimeUnit timeUnit, long duration) {
        long durationMillis = duration == -1L ? -1L : TimeUnit.MILLISECONDS.convert(duration, timeUnit);
        return setCache(cacheKey, cacheValue, durationMillis);
    }

    /**
     * 设置缓存，强制替换
     */
    public V setCache(K cacheKey, V cacheValue, long duration) {
        return setCacheWithAbsent(cacheKey, cacheValue, duration, false);
    }

    /**
     * 设置缓存
     */
    public void setCacheAll(Map<? extends K, ? extends V> m) {
        if (CollectionUtils.isEmpty(m)) {
            return;
        }
        checkSize();
        saveCachesUseLog(new ArrayList<>(m.keySet()));
        currentSize.addAndGet(m.size());
        Long ttlTime = Long.MAX_VALUE;
        Map<K, CacheObj<V>> cacheObjMap = m.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry -> new CacheObj<>(entry.getValue(), ttlTime))));

        cacheObjectMap.putAll(cacheObjMap);
        log.info("have set keys size:" + m.size());
    }

    /**
     * 设置缓存的核心底层方法
     */
    public V setCacheWithAbsent(K cacheKey, V cacheValue, long duration, boolean computeIfAbsent) {
        if (Objects.isNull(cacheKey) || Objects.isNull(cacheValue)) {
            return null;
        }
        Long ttlTime = null;
        if (duration <= 0L) {
            if (duration == -1L) {
                ttlTime = -1L;
            } else {
                return null;
            }
        }
        checkSize();
        saveCacheUseLog(cacheKey);
        currentSize.decrementAndGet();
        if (ttlTime == null) {
            ttlTime = System.currentTimeMillis() + duration;
        }
        CacheObj<V> cacheObj = new CacheObj<>(cacheValue, ttlTime);
        CacheObj<V> nowCacheValue;
        if (!computeIfAbsent) {
            nowCacheValue = cacheObjectMap.compute(cacheKey, (a, b) -> cacheObj);
            return nowCacheValue.getCacheValue();
        }
        nowCacheValue = cacheObjectMap.computeIfAbsent(cacheKey, a -> cacheObj);
        if (nowCacheValue.equals(cacheObj)) {
            //set成功一个新的值
            log.info("have computeIfAbsent set new key:{}", cacheKey);
        }
        return nowCacheValue.getCacheValue();

    }


    /**
     * 获取缓存
     */
    public Long getCacheTTL(K cacheKey) {
        asyncClearCache();
        CacheObj<V> vCacheObj = checkAndGetCache(cacheKey);
        saveCacheUseLog(cacheKey);
        return Objects.nonNull(vCacheObj) ? vCacheObj.getTtlTime() : 0L;
    }

    /**
     * 获取缓存
     */
    public V getCache(K cacheKey) {
        asyncClearCache();
        CacheObj<V> vCacheObj = checkAndGetCache(cacheKey);
        saveCacheUseLog(cacheKey);
        return Objects.nonNull(vCacheObj) ? vCacheObj.getCacheValue() : null;
    }

    /**
     * 获取缓存的map格式数据
     */
    public Map<K, V> getCacheMapType() {
        if (cacheObjectMap.size() <= 0) {
            return Collections.emptyMap();
        }
        Map<K, V> cacheObjMap = new HashMap<>();
        cacheObjectMap.entrySet().stream()
                .filter(entry -> entry.getValue().getTtlTime() > System.currentTimeMillis() || entry.getValue().getTtlTime() == -1L)
                .forEach(entry -> {
                    cacheObjMap.put(entry.getKey(), entry.getValue().CacheValue);
                });
        return cacheObjMap;
    }

    /**
     * 获取缓存
     */
    public List<V> getAllCache(Collection<K> cacheKeys) {
        asyncClearCache();
        return cacheKeys.stream().map((cacheKey) -> {
            CacheObj<V> vCacheObj = checkAndGetCache(cacheKey);
            return Objects.nonNull(vCacheObj) ? vCacheObj.getCacheValue() : null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    public boolean isExist(K cacheKey) {
        return Objects.nonNull(checkAndGetCache(cacheKey));
    }

    /**
     * 删除所有缓存
     */
    public void clear() {
        log.info("have clean all key !");
        cacheObjectMap.clear();
        currentSize.set(0);
    }

    /**
     * 删除某个缓存
     */
    public void deleteCache(K cacheKey) {
        CacheObj<V> cacheValue = cacheObjectMap.remove(cacheKey);
        if (cacheValue != null) {
            log.info("have delete key :" + cacheKey);
            currentSize.decrementAndGet();
        }
    }

    /**
     * 删除多个缓存
     */
    public void deleteAllCache(Collection<K> cacheKeys) {
        cacheKeys.forEach((cacheKey) -> {
            CacheObj<V> cacheValue = cacheObjectMap.remove(cacheKey);
            if (cacheValue != null) {
                log.info("have delete key :" + cacheKey);
                currentSize.decrementAndGet();
            }
        });
    }

    /**
     * 判断缓存在不在,过没过期
     */
    private CacheObj<V> checkAndGetCache(K cacheKey) {
        CacheObj<V> cacheObj = cacheObjectMap.get(cacheKey);
        if (cacheObj == null) {
            return null;
        }
        if (cacheObj.getTtlTime() == -1L) {
            return cacheObj;
        }
        if (cacheObj.getTtlTime() < System.currentTimeMillis()) {
            deleteCache(cacheKey);
            return null;
        }
        return cacheObj;
    }

    /**
     * 删除最近最久未使用的缓存
     */
    private void deleteLRU() {
        log.info("delete Least recently used run!");
        K cacheKey = null;
        synchronized (cacheUseLogList) {
            if (cacheUseLogList.size() >= CACHE_MAX_NUMBER - 10) {
                cacheKey = cacheUseLogList.remove(cacheUseLogList.size() - 1);
            }
        }
        if (cacheKey != null) {
            deleteCache(cacheKey);
        }
    }

    /**
     * 删除过期的缓存
     */
    void deleteTimeOut() {
        List<K> deleteKeyList = new LinkedList<>();
        for (Map.Entry<K, CacheObj<V>> entry : cacheObjectMap.entrySet()) {
            Long ttlTime = entry.getValue().getTtlTime();
            if (ttlTime < System.currentTimeMillis() && ttlTime != -1L) {
                deleteKeyList.add(entry.getKey());
            }
        }
        for (K deleteKey : deleteKeyList) {
            deleteCache(deleteKey);
        }
        int size = deleteKeyList.size();
        if (size > 0) {
            log.info("delete cache count is :" + size);
        }

    }

    /**
     * 检查大小
     * 当当前大小如果已经达到最大大小
     * 首先删除过期缓存，如果过期缓存删除过后还是达到最大缓存数目
     * 删除最久未使用缓存
     */
    private void checkSize() {
        if (currentSize.intValue() >= CACHE_MAX_NUMBER) {
            asyncClearCache();
        }
    }

    /**
     * 保存缓存的使用记录
     */
    private synchronized void saveCacheUseLog(K cacheKey) {
        //TODO 这玩意性能低，本地先凑活用,可使用非阻塞队列
        synchronized (cacheUseLogList) {
            cacheUseLogList.remove(cacheKey);
            cacheUseLogList.add(0, cacheKey);
        }
    }

    /**
     * 保存缓存的使用记录
     */
    private synchronized void saveCachesUseLog(List<K> cacheKeys) {
        //TODO 这玩意性能低，本地先凑活用,可使用非阻塞队列
        synchronized (cacheUseLogList) {
            cacheUseLogList.removeAll(cacheKeys);
            cacheUseLogList.addAll(0, cacheKeys);
        }
    }

    /**
     * 设置清理线程的运行状态为正在运行
     */
    void setCleanThreadStop() {
        cleanThreadIsRun = false;
    }


    @Data
    static class CacheObj<T> {
        /**
         * 缓存对象
         */
        private T CacheValue;
        /**
         * 缓存过期时间
         */
        private Long ttlTime;

        CacheObj(T cacheValue, Long ttlTime) {
            CacheValue = cacheValue;
            this.ttlTime = ttlTime;
        }

        T getCacheValue() {
            return CacheValue;
        }

        Long getTtlTime() {
            return ttlTime;
        }

        @Override
        public String toString() {
            return "CacheObj {" +
                    "CacheValue = " + CacheValue +
                    ", ttlTime = " + ttlTime +
                    '}';
        }
    }
}
 

 
