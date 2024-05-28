package com.ty.mid.framework.service.cache.jcache.listener.through;

import cn.hutool.core.collection.CollUtil;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.service.cache.generic.CacheAutoWrapService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import java.io.Serializable;
import java.util.*;

/**
 * 默认不缓存null或空字符串 <p>
 *
 * @param <K> <p>
 * @param <V>
 */
@Data
@Slf4j
public class ReadThroughLoader<K, V> implements CacheLoader<K, V>, Serializable {
    private CacheAutoWrapService<? extends BaseDO, ? extends BaseIdDO<Long>, ? extends BaseMapperX<?, Long>> cacheAutoWrapService;

    @Override
    public V load(K key) throws CacheLoaderException {
        log.debug("ReadThroughLoader load key:{}", key);
        if (key == null || Objects.equals(key, "")) {
            //默认不缓存空值及空字符串
            return null;
        }
        return GenericsUtil.cast(cacheAutoWrapService.getDbData(String.valueOf(key)));

    }

    @Override
    public Map<K, V> loadAll(Iterable<? extends K> keys) throws CacheLoaderException {
        log.debug("ReadThroughLoader loadAll keys:{}", keys);
        //默认不缓存空值及空字符串
        Iterator<? extends K> iterator = keys.iterator();

        List<String> keyList = new ArrayList<>();
        while (iterator.hasNext()) {
            K next = iterator.next();
            if (next == null || Objects.equals(next, "")) {
                //默认不缓存空值及空字符串
                continue;
            }
            keyList.add(keyList.toString());
        }
        if (CollUtil.isEmpty(keyList)) {
            return Collections.emptyMap();
        }
        Map<String, ? extends BaseIdDO<Long>> dbDataMap = cacheAutoWrapService.getDbDataMap(keyList);
        return GenericsUtil.check2Map(dbDataMap);
    }
}