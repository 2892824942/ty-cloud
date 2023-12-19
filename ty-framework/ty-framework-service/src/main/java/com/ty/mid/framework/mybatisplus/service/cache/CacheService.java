package com.ty.mid.framework.mybatisplus.service.cache;

import com.ty.mid.framework.common.dto.AbstractDTO;
import com.ty.mid.framework.common.entity.AbstractDO;
import com.ty.mid.framework.mybatisplus.service.cache.generic.BaseCacheService;

import java.io.Serializable;

/**
 * 可缓存service的定义
 *
 * @param <T>
 * @param <ID>
 * @param <D>
 */
public interface CacheService<T extends AbstractDO<ID>, ID extends Serializable, D extends AbstractDTO> extends BaseCacheService<T, D> {

}
