package com.ty.mid.framework.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.impl.BeanConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.common.util.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public interface Converter<S, T> {
    default Collection<T> convert2(Collection<S> entities) {
        if (CollUtil.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::convert2).collect(Collectors.toList());
    }

    default Collection<S> rConvert2(Collection<T> entities) {
        if (CollUtil.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::rConvert2).collect(Collectors.toList());
    }

    default T convert2(S entity) {
        return convert2(entity, GenericsUtil.getGenericTypeByIndex(this.getClass(),1));
    }

    default S rConvert2(T dto) {
        return convert2(dto, GenericsUtil.getGenericTypeByIndex(this.getClass(),0));
    }


    default <FR, TO> TO convert2(FR dto, Class<TO> toClass) {
        if (dto == null) {
            return null;
        }
        Validator.requireNonNull(toClass, "to model Class can not be null");
        try {
            BeanConverter<TO> beanConverter = new BeanConverter<>(toClass);
            return beanConverter.convert(dto, null);
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }
}
