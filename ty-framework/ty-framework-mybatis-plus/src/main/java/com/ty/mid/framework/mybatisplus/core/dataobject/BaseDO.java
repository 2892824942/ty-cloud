package com.ty.mid.framework.mybatisplus.core.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.ty.mid.framework.common.entity.Auditable;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体对象
 *
 * @author 芋道源码
 */
@Data
public abstract class BaseDO implements Auditable<Long>, Serializable {

    /**
     * 用户id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 创建者
     * <p>
     */
    @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private Long creator;
    /**
     * 更新者
     * <p>
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
    private Long updater;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;


}
