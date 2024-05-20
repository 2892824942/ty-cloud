package com.ty.mid.framework.web.annotation.desensitize.serializer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.HashIdUtil;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.web.config.WebConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Json数据入参处理
 * 注:支持
 * 1.String入参,其中:支持以,拼接多个入参,最终转换为String
 * 2.数组入参,其中,数组元素必须为String,最终转换为List<Long>
 *
 * 说明:可以将一种的,拼接直接转换为List,但是这种方式会导致api显示的参数为List,而前端需要传String,对于这种让人误解的操作带来的后果,远比此类带来的价值大,
 * 这里不支持这种web传参场景,更改字段类型的操作.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class HashedIdJsonDeserializer extends JsonDeserializer<Object> {
    private WebConfig.HashId hashIdConfig = SpringContextHelper.getBean(WebConfig.class).getHashId();


    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        if (p.hasToken(JsonToken.VALUE_STRING)) {
            if (!hashIdConfig.isEnable()) {
                return p.getText();
            }
            String text = p.getText().trim();

            if (StrUtil.isEmpty(text)) {
                return null;
            }
            if (text.contains(",") && text.split(",").length >= 1) {
                StringJoiner stringJoiner = new StringJoiner(",");
                String[] split = text.split(",");
                for (String s : split) {
                    if (StringUtils.isEmpty(s)) {
                        stringJoiner.add("");
                    } else {
                        try {
                            stringJoiner.add(String.valueOf(HashIdUtil.decode(s, hashIdConfig.getSalt(), hashIdConfig.getMinLength())));
                        } catch (Exception e) {
                            log.warn("resolve origin id for [p:{} ,v:{}] fail, because of parameter value decode error!", p.currentName(), text);
                            throw new FrameworkException("加解密异常[HashedId]");
                        }
                    }
                }
                return stringJoiner.toString();
            }
            return HashIdUtil.decode(text, hashIdConfig.getSalt(), hashIdConfig.getMinLength());
        }
        //支持List序列化
        if (p.currentToken().equals(JsonToken.START_ARRAY)) {
            List<?> dataList = p.readValueAs(ArrayList.class);
            if (CollUtil.isEmpty(dataList)) {
                return null;
            }
            return dataList.stream().map(data -> {
                if (!data.getClass().isAssignableFrom(String.class)) {
                    throw new FrameworkException("解析参数非法[HashedId]");
                }
                return HashIdUtil.decode(String.valueOf(data), hashIdConfig.getSalt(), hashIdConfig.getMinLength());
            }).collect(Collectors.toList());
        }
        throw new FrameworkException("暂只支持String及List<Long>形式![HashedId]");

    }
}
