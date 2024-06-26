package com.ty.mid.framework.security.audit;

import com.ty.mid.framework.common.audit.AuditorInfoResolver;
import com.ty.mid.framework.common.entity.Auditable;
import com.ty.mid.framework.common.model.LoginUser;
import com.ty.mid.framework.security.utils.LoginHelper;

import java.time.LocalDateTime;

/**
 * 审计信息实现类
 */
public class SaTokenAuditorInfoResolver implements AuditorInfoResolver<Long> {

    /**
     * 获取当前用户信息
     *
     * @return Auditable<Long>
     */
    @Override
    public Auditable<Long> resolveCurrentAuditor() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser.isDefaultUser()) {
            return Auditable.DEFAULT_AUDITOR;
        }
        return new Auditable<Long>() {
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
        };
    }

    @Override
    public Auditable<Long> resolveEmptyAuditorInfo() {
        return Auditable.DEFAULT_AUDITOR;
    }
}