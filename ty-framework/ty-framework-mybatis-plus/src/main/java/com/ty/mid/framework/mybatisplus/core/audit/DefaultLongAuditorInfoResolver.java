package com.ty.mid.framework.mybatisplus.core.audit;

import com.ty.mid.framework.common.entity.Auditable;

/**
 * 默认的审计信息实现类,此类目标为快速接入审计能力,实际使用应替换成自己的实现类
 */
public class DefaultLongAuditorInfoResolver implements AuditorInfoResolver<Long> {

    /**
     * 获取当前用户信息
     *
     * @return Auditable<Long>
     */
    @Override
    public Auditable<Long> resolveCurrentAuditor() {
        return Auditable.DEFAULT_AUDITOR;
    }

    @Override
    public Auditable<Long> resolveEmptyAuditorInfo() {
        return Auditable.DEFAULT_AUDITOR;
    }
}