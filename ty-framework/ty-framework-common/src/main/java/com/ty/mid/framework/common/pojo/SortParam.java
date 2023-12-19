package com.ty.mid.framework.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

/**
 * 排序字段 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortParam implements Serializable {

    /**
     * 顺序 - 升序
     */
    public static final String ORDER_ASC = "asc";
    /**
     * 顺序 - 降序
     */
    public static final String ORDER_DESC = "desc";
    /**
     * 排序字段信息
     */
    Collection<SortField> orderFields;

}
