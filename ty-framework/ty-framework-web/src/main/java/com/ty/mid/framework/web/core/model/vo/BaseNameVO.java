package com.ty.mid.framework.web.core.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public abstract class BaseNameVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "创建人姓名")
    private String creatorName;
    @Schema(description = "更新人姓名")
    private String updaterName;
}
