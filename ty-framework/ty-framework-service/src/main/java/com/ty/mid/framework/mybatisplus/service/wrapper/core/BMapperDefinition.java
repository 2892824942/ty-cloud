package com.ty.mid.framework.mybatisplus.service.wrapper.core;

import com.ty.mid.framework.common.entity.BaseIdDO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 常见类自动映射上下文定义
 *
 * @author suyoulinag
 */
@Data
@Slf4j
public class BMapperDefinition {
    private Class<?> returnTypeClass;
    private Class<?> paramTypeClass;
    private AutoWrapper<Object, ? extends BaseIdDO<Long>, Long> autoWrapper;
}
