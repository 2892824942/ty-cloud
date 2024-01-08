package com.ty.mid.framework.service.cache;

import java.util.Set;

public interface ServiceCacheLoader {

    void init();

    Set<String> getCacheNames();

    void reloadCache(String cacheName);

    void clearCache(String cacheName);

}
