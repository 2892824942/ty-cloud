package com.ty.mid.framework.cache.standard;

import com.ty.mid.framework.cache.BaseCacheTests;
import com.ty.mid.framework.cache.service.GlobalCacheTestService;
import com.ty.mid.framework.cache.service.SimpleCacheTestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles()
//@ActiveProfiles()
@Slf4j
public class DefaultCacheTest extends BaseCacheTests {
    @Autowired
    GlobalCacheTestService cacheTestService;

    @Autowired
    SimpleCacheTestService simpleCacheTestService;
    @Autowired
    CacheManager cacheManager;

    @BeforeAll
    public void init() {
        super.cacheTestService = cacheTestService;
        super.cacheManager = cacheManager;
        super.simpleCacheTestService = simpleCacheTestService;
    }

    @Test
    public void doTest() {
        super.doTest();
    }


    @Override
    public void doCacheTest() {
        super.doCacheTest();
    }

    @Test
    public void doCacheInitSyncTest() {
        super.doCacheInitSyncTest();
    }

    @Test
    public void doMultiCacheTest() {
        super.doMultiCacheTest();
    }
}

