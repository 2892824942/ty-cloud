package com.ty.mid.framework.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.ty.mid.framework.common.entity.AbstractDO;
import com.ty.mid.framework.common.entity.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

/**
 * 通用平台实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AbstractBaseDO extends AbstractDO<Long> implements Auditable<Long> {

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
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private Long creator;
    /**
     * 更新者
     * <p>
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
    private Long updater;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
