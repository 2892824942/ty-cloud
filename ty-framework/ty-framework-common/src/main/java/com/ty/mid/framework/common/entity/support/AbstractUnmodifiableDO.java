package com.ty.mid.framework.common.entity.support;

import com.ty.mid.framework.common.entity.AbstractDO;

import java.io.Serializable;

/**
 * 不可变更实体，用于字典表等
 *
 * @param <ID>
 */
public abstract class AbstractUnmodifiableDO<ID extends Serializable> extends AbstractDO<ID> {
}
