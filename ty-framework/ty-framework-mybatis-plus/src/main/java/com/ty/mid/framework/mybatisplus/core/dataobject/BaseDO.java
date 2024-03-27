package com.ty.mid.framework.mybatisplus.core.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import com.ty.mid.framework.common.entity.Auditable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体对象
 *
 * @author suyoulinag
 */
@Setter
public abstract class BaseDO implements Auditable<Long>, Serializable {

    /**
     * 用户id
     */

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private Long creator;
    /**
     * 更新者
     */
    @Schema(description = "更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
    private Long updater;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @Schema(description = "最后更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除 0:未删除 1:已删除
     *
     * @see com.ty.mid.framework.common.constant.DeletedEnum
     */
    @Schema(description = "是否删除 0:未删除 1:已删除")
    @TableLogic
    private Boolean deleted;

    @Override
    public Long getCreator() {
        return creator;
    }

    @Override
    public Long getUpdater() {
        return updater;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    @Override
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    @Override
    public Boolean getDeleted() {
        return deleted;
    }

    @Override
    public Long getId() {
        return id;
    }
}
