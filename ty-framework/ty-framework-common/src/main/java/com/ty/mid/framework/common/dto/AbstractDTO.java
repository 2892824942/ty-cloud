package com.ty.mid.framework.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ty.mid.framework.common.entity.Auditable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class AbstractDTO implements Auditable<Long> {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Long id;

    @JsonIgnore
    private Long creator;

    @JsonIgnore
    private Long updater;

    @JsonIgnore
    private LocalDateTime createTime = LocalDateTime.now();

    @JsonIgnore
    private LocalDateTime updateTime = LocalDateTime.now();

    @JsonIgnore
    private Boolean deleted;

}
