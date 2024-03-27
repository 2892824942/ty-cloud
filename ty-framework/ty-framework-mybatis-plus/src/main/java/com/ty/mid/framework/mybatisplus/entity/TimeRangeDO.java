package com.ty.mid.framework.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ty.mid.framework.common.constant.DomainConstant;
import com.ty.mid.framework.common.entity.Timeliness;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class TimeRangeDO extends BaseDO implements Timeliness {

    @TableField(DomainConstant.Columns.FROM_DATE)
    private LocalDateTime fromDate = LocalDateTime.now();

    @TableField(DomainConstant.Columns.TO_DATE)
    private LocalDateTime toDate;

}
