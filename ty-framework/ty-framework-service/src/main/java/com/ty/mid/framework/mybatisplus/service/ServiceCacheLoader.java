package com.ty.mid.framework.mybatisplus.service;

import java.util.Set;

public interface ServiceCacheLoader {

    void init();

    Set<String> getCacheNames();

    void reloadCache(String cacheName);

    void clearCache(String cacheName);

}
