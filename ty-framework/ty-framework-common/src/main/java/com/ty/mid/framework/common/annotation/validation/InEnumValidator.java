package com.ty.mid.framework.common.annotation.validation;


import com.ty.mid.framework.common.pojo.KVResp;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InEnumValidator implements ConstraintValidator<InEnum, Object> {

    private List<Object> keys;

    @Override
    public void initialize(InEnum annotation) {
        KVResp<?,?>[] kvResps = annotation.value().getEnumConstants();
        if (kvResps.length == 0) {
            this.keys = Collections.emptyList();
        } else {
            this.keys = Arrays.stream(kvResps).map(KVResp::getKey).collect(Collectors.toList());
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 为空时，默认不校验，即认为通过
        if (value == null) {
            return true;
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

