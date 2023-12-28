package com.ty.mid.framework.cache.util;

import cn.hutool.core.collection.CollUtil;
import com.ty.mid.framework.cache.model.NullValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ThreadResourceUtil {
    private static final ThreadLocal<Map<String, Map<Object, Object>>> cacheResources =
            new NamedThreadLocal<>("Transactional cache resources");

    public static boolean hasResource(String name, Object key) {
        log.debug("cacheName:{},key:{},currentThreadId:{},", name, key, Thread.currentThread().getId());
        Object value = getResource(name, key);
        return !Objects.equals(value, null);
    }

    public static Object getResource(String name, Object key) {
        log.debug("cacheName:{},key:{},currentThreadId:{},", name, key, Thread.currentThread().getId());
        Map<String, Map<Object, Object>> map = cacheResources.get();
        if (map == null) {
            return null;
        }
        Map<Object, Object> resourceMap = map.computeIfAbsent(name, k -> new HashMap<>());
        return resourceMap.get(key);
    }

    public static void bindNameSpaceNullValueResource(String name) {
        Map<String, Map<Object, Object>> map = cacheResources.get();
        if (map == null) {
            map = new HashMap<>();
        }
        Map<Object, Object> resourceMap = map.get(name);
        if (!CollUtil.isEmpty(resourceMap)) {
            resourceMap.entrySet().forEach(entry -> entry.setValue(NullValue.INSTANCE));
        }
    }

    public static Object bindResource(String name, Object key, Object value) {
        log.debug("cacheName:{},key:{},currentThreadId:{},", name, key, Thread.currentThread().getId());
        Map<String, Map<Object, Object>> map = cacheResources.get();
        if (map == null) {
            map = new HashMap<>();
            cacheResources.set(map);
        }
        Map<Object, Object> resourceMap = map.computeIfAbsent(name, k -> new HashMap<>());
        return resourceMap.put(key, value);
    }

    public static void unBindResource(String name, Object key) {
        Map<String, Map<Object, Object>> map = cacheResources.get();
        if (map == null) {
            return;
        }
        Map<Object, Object> resourceMap = map.computeIfAbsent(name, k -> new HashMap<>());
        map.remove(key);
    }

    public static void unBindNameSpaceResource(String name) {
        log.debug("cacheName:{},currentThreadId:{},", name, Thread.currentThread().getId());
        Map<String, Map<Object, Object>> map = cacheResources.get();
        if (map == null) {
            return;
        }
        map.remove(name);
    }

    public static void removeAllResource() {
        log.debug("currentThreadId:{},", Thread.currentThread().getId());
        cacheResources.remove();
    }
}
