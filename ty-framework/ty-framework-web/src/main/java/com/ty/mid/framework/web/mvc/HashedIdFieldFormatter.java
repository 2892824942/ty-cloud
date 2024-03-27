package com.ty.mid.framework.web.mvc;

import com.ty.mid.framework.common.util.HashIdUtil;
import com.ty.mid.framework.common.util.collection.MiscUtils;
import com.ty.mid.framework.web.annotation.desensitize.HashedId;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Set;

@Data
@NoArgsConstructor
public class HashedIdFieldFormatter implements AnnotationFormatterFactory<HashedId> {

    private String hashSalt;
    private int minLength = 12;

    public HashedIdFieldFormatter(String hashSalt) {
        this.hashSalt = hashSalt;
    }

    public HashedIdFieldFormatter(String hashSalt, int minLength) {
        this.hashSalt = hashSalt;
        this.minLength = minLength;
    }

    @Override
    public Set<Class<?>> getFieldTypes() {
        return MiscUtils.toSet(Long.class);
    }

    @Override

    public Printer<?> getPrinter(HashedId annotation, Class<?> fieldType) {
        return (Printer<Long>) (object, locale) -> HashIdUtil.encode(object, hashSalt, minLength);
    }

    @Override
    public Parser<?> getParser(HashedId annotation, Class<?> fieldType) {
        return (text, locale) -> HashIdUtil.decode(text, hashSalt, minLength);
    }
}
