package com.ty.mid.framework.encrypt.core.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.encrypt.annotation.EncryptField;
import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.IEncryptor;
import com.ty.mid.framework.encrypt.core.context.CommonEncryptContext;
import com.ty.mid.framework.encrypt.core.context.EncryptContext;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 加密管理类
 *
 * @author suyouliang
 * @version 4.6.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class EncryptorManager {
    protected final EncryptorConfig defaultProperties;

    /**
     * 缓存加密器
     */
    Map<EncryptContext, IEncryptor> encryptorMap = new ConcurrentHashMap<>();

    /**
     * 类加密字段缓存
     */
    Map<Class<?>, Set<Field>> fieldCache = new ConcurrentHashMap<>();

    /**
     * 获取类加密字段缓存
     */
    public Set<Field> getFieldCache(Class<?> sourceClazz) {
        return fieldCache.computeIfAbsent(sourceClazz, clazz -> {
            Field[] declaredFields = clazz.getDeclaredFields();
            Set<Field> fieldSet = Arrays.stream(declaredFields)
                    .filter(field -> Objects.nonNull(AnnotationUtils.findAnnotation(field, EncryptField.class)))
                    .collect(Collectors.toSet());
            for (Field field : fieldSet) {
                field.setAccessible(true);
            }
            return fieldSet;
        });
    }

    /**
     * 注册加密执行者到缓存
     *
     * @param commonEncryptContext 加密执行者需要的相关配置参数
     */
    public IEncryptor registAndGetEncryptor(EncryptContext commonEncryptContext) {
        if (encryptorMap.containsKey(commonEncryptContext)) {
            return encryptorMap.get(commonEncryptContext);
        }
        IEncryptor encryptor = ReflectUtil.newInstance(commonEncryptContext.getAlgorithm().getClazz(), commonEncryptContext);
        encryptorMap.put(commonEncryptContext, encryptor);
        return encryptor;
    }

    /**
     * 移除缓存中的加密执行者
     *
     * @param commonEncryptContext 加密执行者需要的相关配置参数
     */
    public void removeEncryptor(EncryptContext commonEncryptContext) {
        this.encryptorMap.remove(commonEncryptContext);
    }

    /**
     * 根据配置进行加密。会进行本地缓存对应的算法和对应的秘钥信息。
     *
     * @param value          待加密的值
     * @param encryptContext 加密相关的配置信息
     */
    public String encrypt(String value, EncryptContext encryptContext) {
        IEncryptor encryptor = this.registAndGetEncryptor(encryptContext);
        return encryptor.encrypt(value, encryptContext.getEncode());
    }

    /**
     * 根据配置进行解密
     *
     * @param value          待解密的值
     * @param commonEncryptContext 加密相关的配置信息
     */
    public String decrypt(String value, EncryptContext commonEncryptContext) {
        IEncryptor encryptor = this.registAndGetEncryptor(commonEncryptContext);
        return encryptor.decrypt(value);
    }

    /**
     * 加密对象
     *
     * @param sourceObject 待加密对象
     */
    public void encryptHandler(Object sourceObject) {
        if (ObjectUtil.isNull(sourceObject)) {
            return;
        }
        if (sourceObject instanceof Map<?, ?>) {
            new HashSet<>(((Map<?, ?>) sourceObject).values()).forEach(this::encryptHandler);
            return;
        }
        if (sourceObject instanceof List<?>) {
            List<?> sourceList = (List<?>) sourceObject;
            if (CollUtil.isEmpty(sourceList)) {
                return;
            }
            // 判断第一个元素是否含有注解。如果没有直接返回，提高效率
            Object firstItem = sourceList.get(0);
            if (ObjectUtil.isNull(firstItem) || CollUtil.isEmpty(this.getFieldCache(firstItem.getClass()))) {
                return;
            }
            ((List<?>) sourceObject).forEach(this::encryptHandler);
            return;
        }
        Set<Field> fields = this.getFieldCache(sourceObject.getClass());
        try {
            for (Field field : fields) {
                field.set(sourceObject, this.encryptField(Convert.toStr(field.get(sourceObject)), field));
            }
        } catch (Exception e) {
            log.error("处理加密字段时出错", e);
        }
    }


    /**
     * 解密对象
     *
     * @param sourceObject 待加密对象
     */
    public void decryptHandler(Object sourceObject) {
        if (ObjectUtil.isNull(sourceObject)) {
            return;
        }
        if (sourceObject instanceof Map<?, ?>) {
            new HashSet<>(((Map<?, ?>) sourceObject).values()).forEach(this::decryptHandler);
            return;
        }
        if (sourceObject instanceof List<?>) {
            List<?> sourceList = (List<?>) sourceObject;
            if (CollUtil.isEmpty(sourceList)) {
                return;
            }
            // 判断第一个元素是否含有注解。如果没有直接返回，提高效率
            Object firstItem = sourceList.get(0);
            if (ObjectUtil.isNull(firstItem) || CollUtil.isEmpty(this.getFieldCache(firstItem.getClass()))) {
                return;
            }
            ((List<?>) sourceObject).forEach(this::decryptHandler);
            return;
        }
        Set<Field> fields = this.getFieldCache(sourceObject.getClass());
        try {
            for (Field field : fields) {
                field.set(sourceObject, this.decryptField(Convert.toStr(field.get(sourceObject)), field));
            }
        } catch (Exception e) {
            log.error("处理解密字段时出错", e);
        }
    }


    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    public abstract String decryptField(String value, Field field);


    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    public abstract String encryptField(String value, Field field);


}
