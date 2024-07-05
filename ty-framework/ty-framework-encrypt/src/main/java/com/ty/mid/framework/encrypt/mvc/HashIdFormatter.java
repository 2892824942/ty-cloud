package com.ty.mid.framework.encrypt.mvc;

import cn.hutool.core.convert.Convert;
import com.ty.mid.framework.common.util.collection.MiscUtils;
import com.ty.mid.framework.encrypt.annotation.HashedId;
import com.ty.mid.framework.encrypt.core.manager.EncryptorManager;
import lombok.Data;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Set;

/**
 * 对于不经过序列化,入参和出参情况下HashId注解处理
 * 如直接使用Param请求
 * 注:方法注入时已经判断开启Hashed能力,所以方法直接进行数据操作
 * 目前可以处理集合类,Long基本类型,数组等
 * <p>
 * 对于嵌套注解兼容,但是无法获取原始注解(起码我没找到方法获取到),对于web注解加密场景中,仅支持HashedId使用此类是没有问题的
 * 但是考虑嵌套注解场景,没有使用以下方法.更换为:
 *
 * @see EncryptionParserConverter 入参解密转换器 支持嵌套注解
 * 此类暂时不注入系统,暂时保留
 */
@Data
public class HashIdFormatter implements AnnotationFormatterFactory<HashedId> {

    private final EncryptorManager encryptorManager;


    public HashIdFormatter(EncryptorManager encryptorManager) {
        this.encryptorManager = encryptorManager;
    }


    @Override
    public Set<Class<?>> getFieldTypes() {
        return MiscUtils.toSet(Object.class, Long.class, String.class);
    }

    @Override
    public Printer<?> getPrinter(HashedId annotation, Class<?> fieldType) {
        return (object, locale) -> encryptorManager.encrypt(Convert.toStr(object), annotation);
    }

    @Override
    public Parser<?> getParser(HashedId annotation, Class<?> fieldType) {
        return (object, locale) -> encryptorManager.decrypt(Convert.toStr(object), annotation);
    }
}
