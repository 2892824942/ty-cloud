package com.ty.mid.framework.mybatisplus.service.wrapper.core;


import java.lang.annotation.*;

/**
 * @author suyouliang
 * @date 2022/03/26
 * Content :业务实体映射注解
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface BMapping {
    /**
     * 映射的字段Class类型,可多个
     *
     * @return name
     */
    Class<?>[] values();


}
