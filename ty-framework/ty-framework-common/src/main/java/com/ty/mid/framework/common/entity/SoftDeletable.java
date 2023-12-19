package com.ty.mid.framework.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 软删标记
 */
public interface SoftDeletable {

    /**
     * 标记为删除
     */
    void markDeleted();

    /**
     * 数据是否被删除
     *
     * @return
     */
    @JsonIgnore
    boolean isRecordDeleted();

}
