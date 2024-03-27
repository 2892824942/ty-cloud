package com.ty.mid.framework.common.pojo;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(description = "分页结果")
@Data
public final class PageResult<T> implements Serializable {

    @Schema(description = "数据")
    private List<T> list;

    @Schema(description = "总量")
    private Long total;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public static <T> PageResult<T> of(List<T> list, Long total) {
        return new PageResult<>(list, total);
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }


    @JsonIgnore
    public boolean isEmpty() {
        return CollUtil.isEmpty(list);
    }

    @JsonIgnore
    public boolean isNotEmpty() {
        return CollUtil.isNotEmpty(list);
    }

}
