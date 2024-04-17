package com.ty.mid.framework.security.audit;

import com.ty.mid.framework.common.entity.Auditable;
import com.ty.mid.framework.security.utils.LoginHelper;

import java.time.LocalDateTime;

public class SecurityAuditor implements Auditable<Long> {

    @Override
    public Long getCreator() {
        return LoginHelper.getUserId();
    }

    @Override
    public Long getUpdater() {
        return LoginHelper.getUserId();
    }

    @Override
    public LocalDateTime getCreateTime() {
        return LocalDateTime.now();
    }

    @Override
    public LocalDateTime getUpdateTime() {
        return LocalDateTime.now();
    }

    @Override
    public Boolean getDeleted() {
        return Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return LoginHelper.getUserId();
    }
}
