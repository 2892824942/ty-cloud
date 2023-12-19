package com.ty.mid.framework.mybatisplus.audit;

import com.ty.mid.framework.common.entity.Auditable;

import java.io.Serializable;

public interface AuditorInfoResolver<T extends Serializable> {

    /**
     * 获取当前用户信息
     *
     * @return
     */
    Auditable<T> resolveCurrentAuditor();


}