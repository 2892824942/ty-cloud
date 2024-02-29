package com.ty.mid.framework.core.aspect;

import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.collection.MiscUtils;
import com.ty.mid.framework.common.annotation.desensitize.RC4Encrypt;
import com.ty.mid.framework.core.util.DatabaseEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RC4加解密切面
 */
@Aspect
@Slf4j
public class RC4EncryptAspect extends AbstractAspect {

    private static final Set<Class> EXCLUDES = MiscUtils.toSet(
            Long.class, String.class, Short.class, Double.class,
            Float.class, Integer.class, Character.class, Byte.class
    );
    private String privateKey;

    public RC4EncryptAspect(String privateKey) {
        this.privateKey = privateKey;
    }

    public RC4EncryptAspect() {
    }

    @Pointcut("@annotation(com.ty.mid.framework.common.annotation.desensitize.RC4Encrypt)")
    public void rc4EncryptPointcut() {
    }


    @Around("rc4EncryptPointcut()")
    public Object encryptBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (ObjectUtils.isEmpty(args)) {
            return joinPoint.proceed(args);
        }

        Method method = resolveMethod(joinPoint);
        Parameter[] parameters = method.getParameters();
        if (ObjectUtils.isEmpty(parameters)) {
            return joinPoint.proceed(args);
        }

        Object[] newArgs = Arrays.copyOf(args, args.length);
        for (int i = 0; i < args.length; i++) {
            Parameter parameter = parameters[i];

            if (newArgs[i] == null || !parameter.isAnnotationPresent(RC4Encrypt.class)) {
                continue;
            }

            // 字符串类型单独处理
            if (parameter.getType() == String.class
                    && !StringUtils.isEmpty(newArgs[i])
                    && !DatabaseEncryptUtil.isEncryptRC4((String) newArgs[i])) {
                newArgs[i] = DatabaseEncryptUtil.encryptRC4((String) newArgs[i], privateKey);
            }

            // 对象类型，当前仅支持一层，不支持递归
            if (!parameter.getType().isPrimitive()
                    && !parameter.getType().isArray()
                    && !EXCLUDES.contains(parameter.getType())
                    && !Map.class.isAssignableFrom(parameter.getType())
                    && !Collection.class.isAssignableFrom(parameter.getType())) {

                Object object = newArgs[i];
                ReflectionUtils.doWithFields(object.getClass(), new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        field.setAccessible(true);
                        Object fieldVal = ReflectionUtils.getField(field, object);

                        if (Collection.class.isAssignableFrom(field.getType()) && !CollectionUtils.isEmpty((Collection) fieldVal)) {
                            Collection collection = (Collection) fieldVal;
                            if (collection.iterator().next().getClass() == String.class) {
                                List newList = (List) collection.stream().map(c -> {
                                    if (StringUtils.isEmpty(c) || DatabaseEncryptUtil.isEncryptRC4((String) c)) {
                                        return c;
                                    }
                                    return DatabaseEncryptUtil.encryptRC4((String) c, privateKey);
                                }).collect(Collectors.toList());

                                Collection newColl = null;
                                try {
                                    newColl = collection.getClass().newInstance();
                                } catch (InstantiationException e) {
                                    throw new FrameworkException(e);
                                }
                                newColl.addAll(newList);
                                ReflectionUtils.setField(field, object, newColl);
                            }
                        }
                        if (field.getType() == String.class && !StringUtils.isEmpty(fieldVal) && !DatabaseEncryptUtil.isEncryptRC4((String) fieldVal)) {
                            ReflectionUtils.setField(field, object, DatabaseEncryptUtil.encryptRC4((String) fieldVal, privateKey));
                        }
                    }
                }, field -> (field.getType() == String.class || Collection.class.isAssignableFrom(field.getType())) && field.isAnnotationPresent(RC4Encrypt.class));
            }


            // map 处理
            if (Map.class.isAssignableFrom(parameter.getType())
                    && !CollectionUtils.isEmpty((Map) newArgs[i])) {
                RC4Encrypt annotation = parameter.getAnnotation(RC4Encrypt.class);
                String[] values = annotation.value();

                if (ObjectUtils.isEmpty(values)) {
                    continue;
                }

                for (String key : values) {
                    Map map = (Map) newArgs[i];
                    String encrypt = this.encryptValue(map.get(key));
                    if (encrypt != null) {
                        map.put(key, encrypt);
                    }
                }
            }

            // Collection 处理, 目前仅支持 Collection<String> 泛型处理
            if (Collection.class.isAssignableFrom(parameter.getType())
                    && !CollectionUtils.isEmpty((Collection<?>) newArgs[i])) {
                Collection collection = (Collection) newArgs[i];

                if (collection.iterator().next().getClass() == String.class) {
                    List newList = (List) collection.stream().map(c -> {
                        if (StringUtils.isEmpty(c) || DatabaseEncryptUtil.isEncryptRC4((String) c)) {
                            return c;
                        }
                        return DatabaseEncryptUtil.encryptRC4((String) c, privateKey);
                    }).collect(Collectors.toList());

                    Collection newColl = collection.getClass().newInstance();
                    newColl.addAll(newList);
                    newArgs[i] = newColl;
                }
            }

            // array 类型, 仅支持 String 数组
            if (parameter.getType().isArray() && !ObjectUtils.isEmpty((Object[]) newArgs[i])) {
                Object[] array = (Object[]) newArgs[i];
                for (int j = 0; j < array.length; j++) {
                    if (!StringUtils.isEmpty(array[j]) && !DatabaseEncryptUtil.isEncryptRC4((String) array[j])) {
                        array[j] = DatabaseEncryptUtil.encryptRC4((String) array[j], privateKey);
                    }
                }
            }
        }

        return joinPoint.proceed(newArgs);
    }

    // return null if not encrypt
    private String encryptValue(Object value) {
        if (value != null && !StringUtils.isEmpty(value) && !DatabaseEncryptUtil.isEncryptRC4((String) value)) {
            return DatabaseEncryptUtil.encryptRC4((String) value, privateKey);
        }
        return null;
    }
}