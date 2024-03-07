package com.ty.mid.framework.cache.constant;

public enum CachePlusType {
    //系统增强
    /**
     * FrameWork caching using 'Cache' beans from the context.
     */
    REDISSON,
    /**
     * FrameWork caching using 'Cache' beans from the context.
     */
    REDISSON_2PC,

    /**
     * FrameWork caching using 'Cache' beans from the context.
     * with second-caching
     */
    REDISSON_LOCAL_MAP,
    //Spring 官方提供
    /**
     * Generic caching using 'Cache' beans from the context.
     */
    GENERIC,

    /**
     * JCache (JSR-107) backed caching.
     */
    JCACHE,

    /**
     * EhCache backed caching.
     */
    EHCACHE,

    /**
     * Hazelcast backed caching.
     */
    HAZELCAST,

    /**
     * Infinispan backed caching.
     */
    INFINISPAN,

    /**
     * Couchbase backed caching.
     */
    COUCHBASE,

    /**
     * Redis backed caching.
     */
    REDIS,

    /**
     * Caffeine backed caching.
     */
    CAFFEINE,

    /**
     * Simple in-memory caching.
     */
    SIMPLE,

    /**
     * No caching.
     */
    NONE,
    //自定义实现
    /**
     * slef customize impl
     */
    CUSTOMIZE1,
    /**
     * slef customize impl
     */
    CUSTOMIZE2,
    /**
     * slef customize impl
     */
    CUSTOMIZE3,
    /**
     * slef customize impl
     */
    CUSTOMIZE4,
    /**
     * slef customize impl
     */
    CUSTOMIZE5,
    ;
}
