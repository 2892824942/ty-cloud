package com.ty.mid.framework.common.annotation.validation;


import com.ty.mid.framework.common.pojo.KVResp;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {InEnumValidator.class, InEnumCollectionValidator.class}
)
public @interface InEnum {

    /**
     * @return 实现 KVResp 接口的
     */
    Class<? extends KVResp<?, ?>> value();

    String message() default "枚举必须在指定范围 {value}";

    /**
     * 如果为空,是否忽略验证
     * true:忽略
     * false:抛出异常
     *
     * @return
     */
    boolean ifNullIgnore() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
