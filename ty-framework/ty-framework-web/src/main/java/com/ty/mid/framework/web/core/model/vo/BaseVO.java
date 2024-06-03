package com.ty.mid.framework.web.core.model.vo;

import com.ty.mid.framework.common.entity.Auditable;
import com.ty.mid.framework.encrypt.annotation.HashedId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseVO implements Auditable<Long>, Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @HashedId
    private Long id;
    @Schema(description = "创建者id")
    @HashedId
    private Long creator;
    @Schema(description = " 更新者id")
    @HashedId
    private Long updater;
    @Schema(description = "创建时间")
    private LocalDateTime createTime = LocalDateTime.now();
    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime = LocalDateTime.now();
    @Schema(description = "是否删除 0:未删除 1:已删除")
    private Boolean deleted;

    @Override
    public Long getId() {
        return id;
    }
}
