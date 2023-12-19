package com.ty.mid.framework.core;

import cn.hutool.core.convert.impl.BeanConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.core.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public interface Converter<S, T> {
    default Collection<T> convert(Collection<S> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::convert).collect(Collectors.toList());
    }

    default Collection<S> rConvert(Collection<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::rConvert).collect(Collectors.toList());
    }

    default T convert(S entity) {
        return convert(entity, getTargetTypeReference());
    }

    default S rConvert(T dto) {
        return convert(dto, getSourceTypeReference());
    }


    default <FR, TO> TO convert(FR dto, TypeReference<TO> typeReference) {
        if (dto == null) {
            return null;
        }
        Validator.requireNonNull(typeReference, "to model typeReference can not be null");
        try {
            BeanConverter<TO> beanConverter = new BeanConverter<>(typeReference.getType());
            return beanConverter.convert(dto, null);
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }

    /**
     * 使用TypeReference转换，保留全部类型
     *
     * @return
     */
    TypeReference<T> getTargetTypeReference();

    /**
     * 使用TypeReference转换，保留全部类型
     *
     * @return
     */
    TypeReference<S> getSourceTypeReference();


}
