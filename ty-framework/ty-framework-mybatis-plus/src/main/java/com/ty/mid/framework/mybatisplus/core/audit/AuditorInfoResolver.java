package com.ty.mid.framework.mybatisplus.core.audit;

import com.ty.mid.framework.common.entity.Auditable;

import java.io.Serializable;

public interface AuditorInfoResolver<T extends Serializable> {

    /**
     * 获取当前用户信息
     *
     * @return
     */
    Auditable<T> resolveCurrentAuditor();


    /**
     * 当前用户信息为空时处理
     * 比如当系统上下文无法完成审计信息的维护时:使用默认的审计信息,保证数据库审计信息不为null
     * 其他:
     * 当然可以直接在resolveCurrentAuditor中写明为空时的处理逻辑
     * 本方法暴露是为了提醒开发人员,需要关注为空的情况.
     *
     * @return Auditable<T>
     */
    Auditable<T> resolveEmptyAuditorInfo();


}