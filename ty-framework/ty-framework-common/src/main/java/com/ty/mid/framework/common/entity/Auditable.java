package com.ty.mid.framework.common.entity;

import com.ty.mid.framework.common.constant.DefaultTypeConstants;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审计参数 
 */
public interface Auditable<T extends Serializable> extends BaseIdDO<T> {

    Long DEFAULT_USER_ID = DefaultTypeConstants.DEFAULT_LONG;
    /**
     * 默认审计信息
     */
    Auditable<Long> DEFAULT_AUDITOR = new Auditable<Long>() {
        @Override
        public Long getId() {
            return DEFAULT_USER_ID;
        }

        @Override
        public Long getCreator() {
            return DEFAULT_USER_ID;
        }

        @Override
        public Long getUpdater() {
            return DEFAULT_USER_ID;
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
    };

    /**
     * 获取创建人
     *
     * @return
     */
    @NotNull
    T getCreator();

    /**
     * 获取更新人
     *
     * @return
     */
    @NotNull
    T getUpdater();

    /**
     * 获取创建时间
     *
     * @return
     */
    @NotNull
    LocalDateTime getCreateTime();

    /**
     * 获取更新时间
     */
    @NotNull
    LocalDateTime getUpdateTime();

    /**
     * 是否删除
     */
    @NotNull
    Boolean getDeleted();
}
