package com.ty.mid.framework.service.cache.jcache.listener;

import lombok.extern.slf4j.Slf4j;

import javax.cache.event.*;

/**
 * 这个类暂时没用
 *
 * @param <K>
 * @param <V>
 */
@Slf4j
public class CacheListener<K, V> implements CacheEntryCreatedListener<K, V>, CacheEntryUpdatedListener<K, V>, CacheEntryRemovedListener<K, V>, CacheEntryExpiredListener<K, V> {
    @Override
    public void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        log.debug("---------------------");
        for (CacheEntryEvent<? extends K, ? extends V> next : cacheEntryEvents) {
            log.debug("缓存:key:{},value:{},被创建", next.getKey(), next.getValue());
        }
        log.debug("---------------------");

    }

    @Override
    public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        log.debug("---------------------");
        for (CacheEntryEvent<? extends K, ? extends V> next : cacheEntryEvents) {
            log.debug("缓存:key:{},value:{},已过期", next.getKey(), next.getValue());
        }
        log.debug("---------------------");
    }

    @Override
    public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        log.debug("---------------------");
        for (CacheEntryEvent<? extends K, ? extends V> next : cacheEntryEvents) {
            log.debug("缓存:key:{},value:{},已删除", next.getKey(), next.getValue());
        }
        log.debug("---------------------");
    }

    @Override
    public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        log.debug("---------------------");
        for (CacheEntryEvent<? extends K, ? extends V> next : cacheEntryEvents) {
            log.debug("缓存:key:{},value:{},已被更新,以前值:{}", next.getKey(), next.getValue(), next.getOldValue());
        }
        log.debug("---------------------");
    }
}
