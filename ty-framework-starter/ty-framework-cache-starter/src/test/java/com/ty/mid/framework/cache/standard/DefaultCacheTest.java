package com.ty.mid.framework.cache.standard;

import com.ty.mid.framework.cache.BaseCacheTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles()
@Slf4j
public class DefaultCacheTest extends BaseCacheTests {


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

