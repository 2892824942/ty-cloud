package com.ty.mid.framework.web.mvc;

import com.ty.mid.framework.common.util.HashIdUtil;
import com.ty.mid.framework.common.util.collection.MiscUtils;
import com.ty.mid.framework.encrypt.annotation.HashedId;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Set;

/**
 * 对于不经过序列化,入参和出参情况下HashId注解处理
 * 如直接使用Param请求
 * 注:方法注入时已经判断开启Hashed能力,所以方法直接进行数据操作
 * 目前可以处理集合类,Long基本类型,数组等
 */
@Data
@NoArgsConstructor
public class HashedIdFieldFormatter implements AnnotationFormatterFactory<HashedId> {

    private EncryptorConfig encryptorConfig;

    public HashedIdFieldFormatter(EncryptorConfig encryptorConfig) {
        this.encryptorConfig = encryptorConfig;
    }


    @Override
    public Set<Class<?>> getFieldTypes() {
        return MiscUtils.toSet(Long.class);
    }

    @Override

    public Printer<?> getPrinter(HashedId annotation, Class<?> fieldType) {
        EncryptorConfig.HashId hashId = encryptorConfig.getHashId();
        return (Printer<Long>) (object, locale) -> HashIdUtil.encode(object, hashId.getSalt(), hashId.getMinLength());
    }

    @Override
    public Parser<?> getParser(HashedId annotation, Class<?> fieldType) {
        EncryptorConfig.HashId hashId = encryptorConfig.getHashId();
        return (text, locale) -> HashIdUtil.decode(text, hashId.getSalt(), hashId.getMinLength());
    }
}
