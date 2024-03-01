package com.ty.mid.framework.web.annotation.desensitize.serializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.ty.mid.framework.common.util.HashIdUtil;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.web.config.WebConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.IOException;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class HashedIdJsonDeserializer extends JsonDeserializer<Long> {
    private WebConfig.HashId hashIdConfig = SpringContextHelper.getBean(WebConfig.class).getHashId();
    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText().trim();
        if (!hashIdConfig.isEnable()){
            return Long.parseLong(text);
        }

        if (StrUtil.isEmpty(text)) {
            return null;
        }

        return HashIdUtil.decode(text, hashIdConfig.getSalt(), hashIdConfig.getMinLength());
    }
}
