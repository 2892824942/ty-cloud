package com.ty.mid.framework.lock.registry;

import com.ty.mid.framework.core.cache.Cache;
import com.ty.mid.framework.core.cache.support.InMemoryCache;
import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.factory.LockFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public abstract class AbstractCacheLockRegistry extends AbstractDecorateLockRegistry {

    /**
     * 自带过期时间，重置后续期
     */
    private final Cache<String, Lock> locks = new InMemoryCache<>(openClearLRU());
    protected TimeUnit timeUnit = TimeUnit.SECONDS;
    protected long expiration = -1;

    public AbstractCacheLockRegistry(LockFactory lockFactory) {
        super(lockFactory);
    }

    protected boolean openClearLRU() {
        return true;
    }

    /**
     * 覆盖父类写法
     *
     * @param LockInfo lockInfo
     * @return
     */
    @Override
    public Lock doGetLock(LockInfo lockInfo) {
        return this.locks.computeIfAbsent(lockInfo.getName(), super.doGetLock(lockInfo), timeUnit, expiration);
    }


    public static void main(String[] args) {
        Map<String, String> testMap = new ConcurrentHashMap<>();
        String key1 = "aaa";
        String value1 = "AAA";
        String source = testMap.put(key1, value1);
        String put = testMap.put(key1, "put");
        System.out.println("put return:" + put);
        String compute = testMap.compute(key1, (a, b) -> "compute");
        System.out.println("compute return:" + compute);

        String putIfAbsent = testMap.putIfAbsent(key1, "putIfAbsent");
        System.out.println("putIfAbsent return:" + putIfAbsent);
        String computeIfAbsent = testMap.computeIfAbsent(key1, (a) -> "computeIfAbsent");
        System.out.println("computeIfAbsent return:" + computeIfAbsent);

        String key2 = "bbb";
        String putIfAbsent2 = testMap.putIfAbsent(key2, "putIfAbsent2");
        System.out.println("putIfAbsent2 return:" + putIfAbsent2);
        String key3 = "ccc";
        String computeIfAbsent2 = testMap.computeIfAbsent(key3, (a) -> "computeIfAbsent2");
        System.out.println("computeIfAbsent2 return:" + computeIfAbsent2);
    }


}
