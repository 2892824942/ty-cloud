package com.ty.mid.framework.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ty.mid.framework.common.entity.Auditable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class AbstractNameDTO extends AbstractDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "创建人姓名")
    private String creatorName;
    @Schema(description = "更新人姓名")
    private String updaterName;
}
