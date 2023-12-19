package com.ty.mid.framework.cache.support;


import com.ty.mid.framework.core.cache.HashCache;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
@Data
@Slf4j
@Deprecated

public class RedisCache<K, IK, V> implements HashCache<K, IK, V> {
    @Override
    public V get(K key, IK innerKey) {
        return null;
    }

    @Override
    public void put(K key, IK innerKey, V value) {

    }

    @Override
    public void put(K key, Map<IK, V> m) {

    }

    @Override
    public Map<IK, V> putAll(K key, Map<IK, V> m) {
        return null;
    }

    @Override
    public Map<IK, V> getMapAll(K key) {
        return null;
    }

    @Override
    public void invalidate(K key, IK innerKey) {

    }

    @Override
    public Collection<V> getCollection(K key) {
        return null;
    }

    @Override
    public void addAll(K key, Collection<V> value) {

    }

    @Override
    public void add(K key, Collection<V> value) {

    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public boolean existsKey(K key) {
        return false;
    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public void put(K key, V value, TimeUnit timeUnit, Long expiration) {

    }

    @Override
    public V putIfAbsent(K key, V value, TimeUnit timeUnit, Long expiration) {
        return null;
    }

    @Override
    public V computeIfAbsent(K key, V value, TimeUnit timeUnit, Long expiration) {
        return null;
    }

    @Override
    public void put(K key, V value, TimeUnit timeUnit, Date expireAt) {

    }

    @Override
    public List<V> getAll(Collection<K> keys) {
        return null;
    }

    @Override
    public void invalidate(K key) {

    }

    @Override
    public void invalidateAll(Collection<K> keys) {

    }

    @Override
    public Date getExpireAt(K key) {
        return null;
    }

    @Override
    public boolean renewCache(K cacheKey, long renewTime, TimeUnit timeUnit) {
        return false;
    }

//    private RedisTemplate<K, V> redisTemplate;
//
//    public RedisCache(RedisTemplate<K, V> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Override
//    public V get(K key) {
//        return redisTemplate.opsForValue().get(key);
//    }
//
//    @Override
//    public Collection<V> getCollection(K key) {
//        return redisTemplate.opsForList().range(key, 0, Long.MAX_VALUE);
//    }
//
//    @Override
//    public boolean existsKey(K key) {
//        Boolean result = redisTemplate.hasKey(key);
//        return Objects.nonNull(result) ? result : Boolean.FALSE;
//    }
//
//
//    @Override
//    public void put(K key, V value) {
//        redisTemplate.opsForValue().set(key, value);
//    }
//
//    @Override
//    public void put(K key, V value, TimeUnit timeUnit, Long expiration) {
//        redisTemplate.opsForValue().set(key, value, expiration, timeUnit);
//
//    }
//
//    @Override
//    public V computeIfAbsent(K key, V value, TimeUnit timeUnit, Long expiration) {
//        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, expiration, timeUnit);
//        if (Objects.nonNull(result) && result) {
//            return value;
//        }
//        return redisTemplate.opsForValue().get(key);
//    }
//
//    @Override
//    public V putIfAbsent(K key, V value, TimeUnit timeUnit, Long expiration) {
//        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, expiration, timeUnit);
//        if (Objects.nonNull(result) && result) {
//            return null;
//        }
//        return redisTemplate.opsForValue().get(key);
//    }
//
//    @Override
//    public void put(K key, V value, TimeUnit timeUnit, Date expireAt) {
//        long milliSeconds = expireAt.getTime() - System.currentTimeMillis();
//        if (milliSeconds > 0) {
//            putIfAbsent(key, value, TimeUnit.MILLISECONDS, milliSeconds);
//        }
//
//    }
//
//    @Override
//    public void addAll(K key, Collection<V> value) {
//        redisTemplate.opsForList().rightPushAll(key, value);
//    }
//
//    @Override
//    public void add(K key, Collection<V> value) {
//        //调用lua脚本并执行
//        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//        redisScript.setResultType(Long.class);//返回类型是Long
//        //lua文件存放在resources目录下的redis文件夹内
//        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/remove_all_and_add_list.lua")));
//
//        redisTemplate.execute(redisScript, ListUtil.of(key), value);
//    }
//
//
//    @Override
//    public List<V> getAll(Collection<K> keys) {
//        return redisTemplate.opsForValue().multiGet(keys);
//    }
//
//    @Override
//    public void invalidate(K key) {
//        redisTemplate.delete(key);
//    }
//
//    @Override
//    public void invalidateAll(Collection<K> keys) {
//        redisTemplate.delete(keys);
//    }
//
//    @Override
//    public Date getExpireAt(K key) {
//        Long expire = this.redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
//        if (expire == null || expire <= 0) {
//            return null;
//        }
//        return new Date(System.currentTimeMillis() + expire);
//    }
//
//    @Override
//    public boolean renewCache(K cacheKey, long renewTime, TimeUnit timeUnit) {
//        if (!this.existsKey(cacheKey)) {
//            return false;
//        }
//
//        Long expire = this.redisTemplate.getExpire(cacheKey);
//
//        long seconds = timeUnit.toSeconds(renewTime);
//        Boolean result = redisTemplate.expire(cacheKey, seconds + Optional.ofNullable(expire).orElse(0L), TimeUnit.SECONDS);
//        return Objects.nonNull(result) ? result : Boolean.FALSE;
//    }
//
//    @Override
//    public V get(K key, IK innerKey) {
//        HashOperations<K, IK, V> hashOperations = redisTemplate.opsForHash();
//        return hashOperations.get(key, innerKey);
//    }
//
//    @Override
//    public void put(K key, IK innerKey, V value) {
//        HashOperations<K, IK, V> hashOperations = redisTemplate.opsForHash();
//        hashOperations.put(key, innerKey, value);
//    }
//
//    @Override
//    public void put(K key, Map<IK, V> m) {
//        //调用lua脚本并执行
//        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//        redisScript.setResultType(Long.class);//返回类型是Long
//        //lua文件存放在resources目录下的redis文件夹内
//        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/remove_all_and_put_all_map.lua")));
//        redisTemplate.execute(redisScript, ListUtil.of(key), m);
//    }
//
//    @Override
//    public Map<IK, V> putAll(K key, Map<IK, V> m) {
//        HashOperations<K, IK, V> hashOperations = redisTemplate.opsForHash();
//        hashOperations.putAll(key, m);
//        return hashOperations.entries(key);
//    }
//
//
//    @Override
//    public Map<IK, V> getMapAll(K key) {
//        HashOperations<K, IK, V> hashOperations = redisTemplate.opsForHash();
//        return hashOperations.entries(key);
//    }
//
//    @Override
//    public void invalidate(K key, IK innerKey) {
//        HashOperations<K, IK, V> hashOperations = redisTemplate.opsForHash();
//        hashOperations.delete(key, innerKey);
//    }
}
