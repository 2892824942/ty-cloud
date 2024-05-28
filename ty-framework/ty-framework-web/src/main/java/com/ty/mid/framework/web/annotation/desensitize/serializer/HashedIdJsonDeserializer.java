package com.ty.mid.framework.web.annotation.desensitize.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.common.util.HashIdUtil;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.web.config.WebConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Json数据入参处理
 * 注:支持
 * 1.String入参,其中:支持以,拼接多个入参,最终转换为String
 * 2.数组入参,其中,数组元素必须为String,最终转换为List<Long>
 * <p>
 * 说明:可以将一种的,拼接直接转换为List,但是这种方式会导致api显示的参数为List,而前端需要传String,对于这种让人误解的操作带来的后果,远比此类带来的价值大,
 * 这里不支持这种web传参场景,更改字段类型的操作.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class HashedIdJsonDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {
    private WebConfig.HashId hashIdConfig = SpringContextHelper.getBean(WebConfig.class).getHashId();
    private Class<?> paramType;
    private String paramName;

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        if (p.hasToken(JsonToken.VALUE_STRING)) {
            if (!hashIdConfig.isEnable()) {
                return p.getText();
            }
            String text = p.getText().trim();
            return HashIdUtil.decodeString(text, paramName, hashIdConfig.getSalt(), hashIdConfig.getMinLength());
        }
        //支持集合序列化
        if (p.currentToken().equals(JsonToken.START_ARRAY)) {
            List<?> dataList = p.readValueAs(ArrayList.class);
            Class<? extends Collection<Long>> paramHandleClass = GenericsUtil.cast2Class(paramType);
            return HashIdUtil.decodeCollection(dataList, paramName, paramHandleClass, hashIdConfig.getSalt(), hashIdConfig.getMinLength());
        }
        throw new FrameworkException("返序列化暂只支持String及List<Long>形式![HashedId]");

    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        // 获取参数的类型
        this.paramType = property.getType().getRawClass();
        this.paramName = property.getFullName().getSimpleName();
        return this;
    }
}
