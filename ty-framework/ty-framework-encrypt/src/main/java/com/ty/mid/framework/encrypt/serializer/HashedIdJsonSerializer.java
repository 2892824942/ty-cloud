package com.ty.mid.framework.encrypt.serializer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.common.util.HashIdUtil;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Json数据出参处理
 * 注:支持
 * 1.Long类型
 * 2.? extends Collection<Long> 类型
 * 3.String类型,实际值为Long类型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class HashedIdJsonSerializer extends JsonSerializer<Object> {

    private EncryptorConfig.HashId hashIdConfig = SpringContextHelper.getBean(EncryptorConfig.class).getHashId();

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            return;
        }
        if (!hashIdConfig.isEnable()) {
            gen.writeString(value.toString());
            return;
        }
        Assert.isTrue(Long.class.isAssignableFrom(value.getClass()) || Collection.class.isAssignableFrom(value.getClass())
                || String.class.isAssignableFrom(value.getClass()), "当前类型:{},仅支持Long,集合,或类型为String的Long数据使用HashId注解", value.getClass());
        String out = "";
        if (Long.class.isAssignableFrom(value.getClass())) {
            out = HashIdUtil.encode((Long) value, hashIdConfig.getSalt(), hashIdConfig.getMinLength());
            gen.writeString(out);
            return;
        }
        //兼容类型为String,实际数据为Long的情况
        if (String.class.isAssignableFrom(value.getClass())) {
            try {
                long longValue = Long.parseLong((String) value);
                out = HashIdUtil.encode(longValue, hashIdConfig.getSalt(), hashIdConfig.getMinLength());
                gen.writeString(out);
                return;
            } catch (NumberFormatException e) {
                throw new FrameworkException("类型为String,实际应为Long数据才可以使用HashId注解");
            }
        }
        //兼容集合HashId加密
        Collection<?> collection = GenericsUtil.check2Collection(value);
        if (CollUtil.isNotEmpty(collection)) {
            List<String> result = collection.stream().map(innerValue -> {
                Assert.isTrue(Long.class.isAssignableFrom(innerValue.getClass()), "集合泛型仅Long类型可使用HashId注解");
                return HashIdUtil.encode(GenericsUtil.cast(innerValue), hashIdConfig.getSalt(), hashIdConfig.getMinLength());
            }).collect(Collectors.toList());
            String[] array = result.toArray(new String[0]);
            gen.writeArray(array, 0, result.size());
        }
    }
}
