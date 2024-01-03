package com.ty.mid.framework.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;

public abstract class AbstractGenericService<T extends BaseDO, M extends BaseMapperX<T, Long>> extends ServiceImpl<M, T> implements IService<T> {

    /**
     * id 是否空
     * <p>
     * id != null 且  id != 0
     *
     * @param id
     * @return
     */
    public boolean isIdNull(Long id) {
        return id == null || id.equals(0L);
    }

}
