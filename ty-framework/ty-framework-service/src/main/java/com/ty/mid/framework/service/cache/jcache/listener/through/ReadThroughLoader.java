package com.ty.mid.framework.service.cache.jcache.listener.through;

import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.service.cache.generic.CacheService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

@Data
@Slf4j
public class ReadThroughLoader<K, V> implements CacheLoader<K, V>, Serializable {
    private CacheService<? extends BaseDO, ? extends BaseIdDO<Long>, ? extends BaseMapperX<?, Long>> cacheService;

    @Override
    public V load(K key) throws CacheLoaderException {
        log.debug("ReadThroughLoader load key:{}", key);
        return GenericsUtil.cast(cacheService.getDbData(String.valueOf(key)));

    }

    @Override
    public Map<K, V> loadAll(Iterable<? extends K> keys) throws CacheLoaderException {
        log.debug("ReadThroughLoader loadAll keys:{}", keys);
        Iterator<String> keyIt = GenericsUtil.cast(keys.iterator());
        Map<String, ? extends BaseIdDO<Long>> dbDataMap = cacheService.getDbDataMap(keyIt);
        return GenericsUtil.check2Map(dbDataMap);
    }
}