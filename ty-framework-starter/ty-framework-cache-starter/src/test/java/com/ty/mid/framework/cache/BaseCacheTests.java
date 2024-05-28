package com.ty.mid.framework.cache;

import com.ty.mid.framework.cache.service.GlobalCacheTestService;
import com.ty.mid.framework.cache.service.SimpleCacheTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

@SpringBootTest(classes = CacheTestApplication.class)
@Slf4j
public class BaseCacheTests {

    @Autowired
    GlobalCacheTestService cacheTestService;

    @Autowired
    SimpleCacheTestService simpleCacheTestService;
    @Autowired
    CacheManager cacheManager;

    //    @Autowired
//    GlobalCacheTestService cacheTestService;
    public void doTest() {
        String key1 = "aaa";
        String value1 = "1";
        String key2 = "bbb";
        String value2 = "2";
        log.info("cacheManager:{}", cacheManager);
        cacheTestService.save(key1, value1);

        String value = cacheTestService.get(key1);
        log.info("get value key1:{}, value：{}", key1, value);
        String valueCache = cacheTestService.getWithCache(key1);

        log.info("get valueCache key1:{}, value：{}", key1, valueCache);

        cacheTestService.update(key1, value2);
        String valueCacheAfterUpdate = cacheTestService.getWithCache(key1);
        log.info("get cache key1:{}, value after update：{}", key1, valueCacheAfterUpdate);
        cacheTestService.delete(key1);

        String valueCacheAfterDelete = cacheTestService.getWithCache(key1);
        log.info("get cache key1:{}, value after delete：{}", key1, valueCacheAfterDelete);
    }

    /**
     * !!!!!!!!!!!!!!!注意!!!!!!!!!!!!
     * [@CachePut] 注解缓存的value，是以方法的返回值作为标准的
     * 如果数据put进去是"aaa",返回个"bbb",则实际缓存的是后者，导致数据错乱
     */
    public void doCacheTest() {
        log.info("cacheManager:{}", cacheManager);
        String key1 = "aaa";
        String value1 = "1";
        String key2 = "bbb";
        String value2 = "2";
        cacheTestService.save(key1, value1);

        String value = cacheTestService.get(key1);
        log.info("get value key1:{}, value：{}", key1, value);
        String valueCache = cacheTestService.getWithCache(key1);

        log.info("get valueCache key1:{}, value：{}", key1, valueCache);

        cacheTestService.updateNoMatch(key1, value2);
        String valueCacheAfterUpdate = cacheTestService.getWithCache(key1);
        log.info("get cache key1:{}, value after update：{}", key1, valueCacheAfterUpdate);
        cacheTestService.delete(key1);

        String valueCacheAfterDelete = cacheTestService.getWithCache(key1);
        log.info("get cache key1:{}, value after delete：{}", key1, valueCacheAfterDelete);
    }


    public void doCacheInitSyncTest() {
        String key1 = "aaa";
        String value1 = "1";
        String key2 = "bbb";
        String value2 = "2";
        cacheTestService.save(key1, value1);

        String value = cacheTestService.get(key1);
        log.info("get value key1:{}, value：{}", key1, value);
        String valueCache = cacheTestService.getWithCache(key1);

        cacheTestService.delete(key1);

        String valueCacheAfterDelete = cacheTestService.getWithSyncInitCache(key1);
        log.info("get cache key1:{}, value after delete：{}", key1, valueCacheAfterDelete);
    }

    public void doMultiCacheTest() {
        String key1 = "aaa";
        String value1 = "1";
        String key2 = "bbb";
        String value2 = "2";
        log.info("cacheManager:{}", cacheManager);
        cacheTestService.save(key1, value1);

        String value = cacheTestService.get(key1);
        log.info("get globalCacheTestService value key1:{}, value：{}", key1, value);


        simpleCacheTestService.save(key1, value1);
        String valueSimple = simpleCacheTestService.get(key1);
        log.info("get value simpleCacheTestService key1:{}, value：{}", key1, valueSimple);
        String valueCache = cacheTestService.getWithCache(key1);

        cacheTestService.delete(key1);

        String valueCacheAfterDelete = cacheTestService.getWithSyncInitCache(key1);
        log.info("get globalCacheTestService cache key1:{}, value after delete：{}", key1, valueCacheAfterDelete);

        String valueNew = simpleCacheTestService.getWithCache(key1);

        log.info("get simpleCacheTestService cache key1:{}, value after other delete：{}", key1, valueNew);

        simpleCacheTestService.delete(key1);
        String valueAfterDelete = simpleCacheTestService.getWithCache(key1);
        log.info("get simpleCacheTestService cache key1:{}, value after self delete：{}", key1, valueAfterDelete);
    }


    public void doSimpleTxTest() {
        String key1 = "aaa";
        String value1 = "1";
        String key2 = "bbb";
        String value2 = "2";
        cacheTestService.save(key1, value1);

        String value = cacheTestService.get(key1);
        log.info("get globalCacheTestService value key1:{}, value：{}", key1, value);


        simpleCacheTestService.save(key1, value1);
        String valueSimple = simpleCacheTestService.get(key1);
        log.info("get value simpleCacheTestService key1:{}, value：{}", key1, valueSimple);
        String valueCache = cacheTestService.getWithCache(key1);

        cacheTestService.delete(key1);

        String valueCacheAfterDelete = cacheTestService.getWithSyncInitCache(key1);
        log.info("get globalCacheTestService cache key1:{}, value after delete：{}", key1, valueCacheAfterDelete);

        String valueNew = simpleCacheTestService.getWithCache(key1);

        log.info("get simpleCacheTestService cache key1:{}, value after other delete：{}", key1, valueNew);

        simpleCacheTestService.delete(key1);
        String valueAfterDelete = simpleCacheTestService.getWithCache(key1);
        log.info("get simpleCacheTestService cache key1:{}, value after self delete：{}", key1, valueAfterDelete);
    }


}
