package com.ty.mid.framework.encrypt.mvc;

import cn.hutool.core.convert.Convert;
import com.ty.mid.framework.encrypt.annotation.EncryptField;
import com.ty.mid.framework.encrypt.core.manager.EncryptorManager;
import lombok.Data;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
/**
 * 对于不经过序列化,入参和出参情况下解密处理
 * 如直接使用Param请求
 * 注:
 * 1.目前可以处理集合类,Long基本类型,数组等
 *
 * 2.对于嵌套注解兼容,目前HashedId注解测试无问题,对于EncryptField注解及其子注解同样生效,由于EncryptField注解使用繁琐,暂时不支持param标注,
 * 理论上,开启param标注权限,同样可以支持
 */
@Data
public class EncryptionParserConverter implements GenericConverter {
    private EncryptorManager encryptorManager;

    public EncryptionParserConverter(EncryptorManager encryptorManager) {
        this.encryptorManager = encryptorManager;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> convertiblePairs = new HashSet<>();
        convertiblePairs.add(new ConvertiblePair(String.class, Long.class));
        convertiblePairs.add(new ConvertiblePair(String.class, String.class));
        return convertiblePairs;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Annotation[] annotations = targetType.getAnnotations();
        if (annotations.length <= 0) {
            return source;
        }
        //加密注解暂时只支持同时标注一个,可以是EncryptField注解,或者以EncryptField为元注解的注解
        Optional<Annotation> targetAnnotation = Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(EncryptField.class) ||
                        annotation.annotationType().isAssignableFrom(EncryptField.class))
                .findFirst();
        if (!targetAnnotation.isPresent()) {
            return source;
        }
        Annotation annotation = targetAnnotation.get();
        String decryptValue = encryptorManager.decrypt(Convert.toStr(source), annotation);
        if (targetType.getType() == Long.class) {
            return Long.parseLong(decryptValue);
        }
        return decryptValue;
    }
}
