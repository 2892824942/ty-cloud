package com.ty.mid.framework.common.annotation.validation;


import com.ty.mid.framework.common.pojo.KVResp;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InEnumValidator implements ConstraintValidator<InEnum, Object> {

    private List<Object> keys;

    private InEnum inEnum;

    @Override
    public void initialize(InEnum annotation) {
        KVResp<?, ?>[] kvResps = annotation.value().getEnumConstants();
        if (kvResps.length == 0) {
            this.keys = Collections.emptyList();
        } else {
            this.keys = Arrays.stream(kvResps).map(KVResp::getKey).collect(Collectors.toList());
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 为空时，根据属性判断是否通过，默认不通过,默认情况下无需配合NotNull使用
        if (value == null) {
            return inEnum.ifNullIgnore();
        }
        // 校验通过
        if (keys.contains(value)) {
            return true;
        }
        // 校验不通过，自定义提示语句（因为，注解上的 value 是枚举类，无法获得枚举类的实际值）
        context.disableDefaultConstraintViolation(); // 禁用默认的 message 的值
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()
                .replaceAll("\\{value}", keys.toString())).addConstraintViolation(); // 重新添加错误提示语句
        return false;
    }

}

