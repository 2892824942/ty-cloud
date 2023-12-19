package com.ty.mid.framework.lock.config.spi;

import cn.hutool.core.collection.CollectionUtil;

import java.util.*;

public class LockSpiClassLoader {
    static LockSpiClassLoader lockSpIClassLoaderInstance;
    Map<String, List<Object>> loaderClassMap = new HashMap<>();

    public static LockSpiClassLoader getInstance() {
        if (lockSpIClassLoaderInstance != null) {
            return lockSpIClassLoaderInstance;
        }
        lockSpIClassLoaderInstance = new LockSpiClassLoader();
        return lockSpIClassLoaderInstance;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> loaderClass(Class<T> classNeedLoad) {
        if (Objects.isNull(classNeedLoad)) {
            return Collections.emptyList();
        }
        ServiceLoader<T> serviceLoader = ServiceLoader.load(classNeedLoad);
        String name = classNeedLoad.getName();
        List<Object> loadClassList = loaderClassMap.get(name);
        if (CollectionUtil.isEmpty(loadClassList)) {
            loadClassList = new ArrayList<>();
            loaderClassMap.put(name, loadClassList);
        }
        if (CollectionUtil.isEmpty(loadClassList)) {
            Iterator<T> iterator = serviceLoader.iterator();
            while (iterator.hasNext()) {
                T next = iterator.next();
                loadClassList.add(next);
            }
        }
        return (List<T>) loadClassList;

    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getLoadingClass(Class<T> classNeedLoad) {
        if (Objects.isNull(classNeedLoad)) {
            return null;
        }
        return (List<T>) loaderClassMap.get(classNeedLoad.getName());

    }
}
