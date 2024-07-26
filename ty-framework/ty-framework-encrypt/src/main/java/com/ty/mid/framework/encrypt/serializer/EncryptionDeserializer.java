package com.ty.mid.framework.encrypt.serializer;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.encrypt.annotation.EncryptField;
import com.ty.mid.framework.encrypt.core.manager.CommonEncryptorManager;
import com.ty.mid.framework.encrypt.core.manager.EncryptorManager;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;


/**
 * 解密序列化器
 * 作用:序列化过程中,解密标注了加解密注解的字段
 * Json数据入参处理
 * 注:支持
 * 1.String入参,其中:支持以,拼接多个入参,最终转换为String
 * 2.数组入参,其中,数组元素必须为String,最终转换为List<Long>
 * <p>
 * 说明:可以将一种的,拼接直接转换为List,但是这种方式会导致api显示的参数为List,而前端需要传String,对于这种让人误解的操作带来的后果,远比此类带来的价值大,
 * 这里不支持这种web传参场景,更改字段类型的操作.
 *
 * @author suyouliang
 */
@Getter
@SuppressWarnings("rawtypes")
public class EncryptionDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {
    EncryptorManager encryptorManager = SpringContextHelper.getBeanSafety(EncryptorManager.class);
    private Class<?> paramType;
    private String paramName;

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (Objects.isNull(encryptorManager)){
            //依赖了加密模块,但是关闭了加密功能,直接跳过
            return p.getCurrentValue();
        }
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String text = p.getText().trim();
            if (StrUtil.isBlank(text)) {
                return text;
            }
            // 获取序列化字段
            Field field = getField(p);
            // 自定义处理器
            EncryptField[] annotations = AnnotationUtil.getCombinationAnnotations(field, EncryptField.class);
            if (ArrayUtil.isEmpty(annotations)) {
                return text;
            }
            return encryptorManager.decryptField(text, field);
        }
        //支持集合序列化
        if (p.currentToken().equals(JsonToken.START_ARRAY)) {
            Collection<?> dataList = p.readValueAs(Collection.class);
            // 获取序列化字段
            Field field = getField(p);

            return encryptorManager.decryptField(dataList, field);
        }
        throw new FrameworkException("反序列化暂只支持String及List<Long>形式![HashedId]");

    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        // 获取参数的类型
        this.paramType = property.getType().getRawClass();
        this.paramName = property.getFullName().getSimpleName();
        return this;
    }

    /**
     * 获取字段
     *
     * @param p JsonParser
     * @return 字段
     */
    private Field getField(JsonParser p) {
        Object currentValue = p.getCurrentValue();
        Class<?> currentValueClass = currentValue.getClass();
        return ReflectUtil.getField(currentValueClass, paramName);
    }
}
