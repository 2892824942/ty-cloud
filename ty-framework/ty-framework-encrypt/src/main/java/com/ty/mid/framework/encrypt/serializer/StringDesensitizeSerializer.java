package com.ty.mid.framework.encrypt.serializer;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.encrypt.annotation.Desensitize;
import com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler.DesensitizationHandler;
import com.ty.mid.framework.encrypt.core.manager.DesensitizeEncryptorManager;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 脱敏序列化器 <p>
 * 实现 JSON 返回数据时，使用 {@link DesensitizationHandler} 对声明脱敏注解的字段，进行脱敏处理。 <p>
 *
 * @author suyouliang
 */
@Getter
@SuppressWarnings("rawtypes")
public class StringDesensitizeSerializer extends StdSerializer<String> {

    private final DesensitizeEncryptorManager encryptorManager = SpringContextHelper.getBean(DesensitizeEncryptorManager.class);

    protected StringDesensitizeSerializer() {
        super(String.class);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (StrUtil.isBlank(value)) {
            gen.writeNull();
            return;
        }
        // 获取序列化字段
        Field field = getField(gen);

        // 自定义处理器
        Desensitize[] annotations = AnnotationUtil.getCombinationAnnotations(field, Desensitize.class);
        if (ArrayUtil.isEmpty(annotations)) {
            gen.writeString(value);
            return;
        }
        encryptorManager.encryptField(value, field);
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
