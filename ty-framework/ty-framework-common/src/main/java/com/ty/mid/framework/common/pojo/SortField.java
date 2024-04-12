package com.ty.mid.framework.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 排序字段 DTO
 * <p>
 * 类名加了 ing 的原因是，避免和 ES SortField 重名。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortField implements Serializable {
    /**
     * 字段
     */
    @Schema(description = "字段名")
    private String field;
    /**
     * 顺序
     */
    @Schema(description = "顺序(无视大小写) asc:升序 desc:降序 ")
    private String order;

}
