package com.ty.mid.framework.mybatisplus.service.wrapper.core;

import com.ty.mid.framework.common.entity.BaseIdDO;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * 可缓存service的定义
 *
 * @param <T>
 * @param <ID>
 */
public interface AutoWrapper<S, T extends BaseIdDO<ID>, ID extends Serializable> {

    Map<Object, T> covert(Collection<S> keyList);

}