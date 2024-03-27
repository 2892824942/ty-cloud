package com.ty.mid.framework.web.annotation.desensitize.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
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
public class HashedIdJsonSerializer extends JsonSerializer<Long> {

    private WebConfig.HashId hashIdConfig = SpringContextHelper.getBean(WebConfig.class).getHashId();

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (!hashIdConfig.isEnable()) {
            gen.writeString(value == null ? null : value.toString());
            return;
        }
        String out = value == null ? null : HashIdUtil.encode(value, hashIdConfig.getSalt(), hashIdConfig.getMinLength());
        gen.writeString(out);
    }
}
