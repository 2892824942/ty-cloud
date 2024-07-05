package com.ty.mid.framework.encrypt.serializer;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.encrypt.annotation.EncryptField;
import com.ty.mid.framework.encrypt.core.manager.CommonEncryptorManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 加密序列化器
 * 作用:序列化过程中,加密标注了加解密注解的字段
 *
 * @author suyouliang
 */
@Getter
@Slf4j
@SuppressWarnings("rawtypes")
public class EncryptionSerializer extends StdSerializer<Object> {

    private final CommonEncryptorManager encryptorManager = SpringContextHelper.getBean(CommonEncryptorManager.class);

    protected EncryptionSerializer() {
        super(Object.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (Objects.isNull(value)) {
            gen.writeNull();
            return;
        }
        // 获取序列化字段
        Field field = getField(gen);

        // 自定义处理器
        EncryptField[] annotations = AnnotationUtil.getCombinationAnnotations(field, EncryptField.class);
        if (ArrayUtil.isEmpty(annotations)) {
            gen.writeString(Convert.toStr(value));
            return;
        }
        //兼容类型为String,实际数据为Long的情况
        if (String.class.isAssignableFrom(value.getClass()) || Long.class.isAssignableFrom(value.getClass())) {
            String encryptValue = encryptorManager.encryptField(Convert.toStr(value), field);
            gen.writeString(encryptValue);
            return;
        }
        if (Collection.class.isAssignableFrom(value.getClass())) {
            //兼容集合HashId加密

            Collection<?> collection = GenericsUtil.check2Collection(value);
            if (CollUtil.isNotEmpty(collection)) {
                Collection<String> transCollection = CollUtil.trans(collection, Convert::toStr);
                List<String> result = encryptorManager.encryptField(transCollection, field);
                String[] array = result.toArray(new String[0]);
                gen.writeArray(array, 0, result.size());
                return;
            }
        }
        log.warn("no support encrypt situation,value:{},field:{}", value, field);
        throw new FrameworkException("不兼容的加密类型,仅支持单独的String,Long或者集合形式");
    }

    /**
     * 获取字段
     *
     * @param generator JsonGenerator
     * @return 字段
     */
    private Field getField(JsonGenerator generator) {
        String currentName = generator.getOutputContext().getCurrentName();
        Object currentValue = generator.getCurrentValue();
        Class<?> currentValueClass = currentValue.getClass();
        return ReflectUtil.getField(currentValueClass, currentName);
    }

}
