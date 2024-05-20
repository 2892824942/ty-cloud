package com.ty.mid.framework.service.wrapper.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.ty.mid.framework.common.dto.BaseNameDTO;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.common.util.collection.CollectionUtils;
import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.service.wrapper.AutoWrapService;
import com.ty.mid.framework.service.wrapper.UserNameTranslation;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.MappingTarget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 业务常用实体自动装载器
 */
@Slf4j
public class MappingProvider {
    private static final Map<Class<?>, Method> CONTAINER = new HashMap<>();

    public static Map<Class<?>, Method> getContainer() {
        return MappingProvider.CONTAINER;
    }

    public static <S extends BaseIdDO<Long>, T extends BaseIdDO<Long>> void autoWrapper(S source, T target) {
        autoWrapper(Collections.singletonList(source), Collections.singletonList(target));
    }

    @SuppressWarnings("unchecked")
    public static <S extends BaseIdDO<Long>, T extends BaseIdDO<Long>> void autoWrapper(Collection<S> sourceList, Collection<T> targetList) {
        if (CollUtil.isEmpty(sourceList) || CollUtil.isEmpty(targetList)) {
            return;
        }
        S sourceFirst = sourceList.iterator().next();
        List<Field> bMappingFieldList = MappingProvider.getBMappingField(sourceFirst);
        if (CollUtil.isEmpty(bMappingFieldList)) {
            return;
        }

        //获取定义的AbstractAutoWrapService,转化为  Map<可自动装载Class,AutoWrapper.Field>
        Map<String, AutoWrapper> wrapServiceMap = SpringContextHelper.getBeansOfType(AutoWrapper.class);
        Map<Class<?>, AutoWrapper> autoWrapServiceMap = getAbstractAutoWrapService(wrapServiceMap);


        T targetFirst = targetList.iterator().next();
        for (Field sourceField : bMappingFieldList) {
            AutoWrap annotation = sourceField.getAnnotation(AutoWrap.class);
            //1.校验
            Class<?>[] autoWrapperClassArray = annotation.values();
            List<Class<?>> targetWrapperCandidates = Arrays.stream(autoWrapperClassArray)
                    .filter(autoWrapperClass -> Objects.nonNull(autoWrapServiceMap.get(autoWrapperClass)))
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(targetWrapperCandidates)) {
                //未找到对应的自动装载器
                log.debug("target class not found field when auto covert process.check out you target:{} whether forgot define " +
                        ",or make a mistake value Class on  @AutoWrap in source:{} ", targetFirst.getClass(), sourceList.iterator().next().getClass());

                continue;

            }
            Map<Field, Class<?>> targetClassFieldMap = new HashMap<>();
            targetWrapperCandidates.forEach(wrapperCandidate -> {
                Field field = MappingProvider.getFieldByClass(targetFirst, wrapperCandidate);
                if (Objects.nonNull(field)) {
                    targetClassFieldMap.put(field, wrapperCandidate);
                }
            });

            if (CollUtil.isEmpty(targetClassFieldMap)) {
                //当前目标类中没有指定的需要映射的对象
                continue;
            }
            //2.组装映射
            //获取value
            sourceField.setAccessible(Boolean.TRUE);
            Map<Long, Object> idKeyMap = new HashMap<>();
            sourceList.forEach(inSource -> {
                Object key;
                try {
                    key = sourceField.get(inSource);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (Objects.nonNull(key)) {
                    idKeyMap.put(inSource.getId(), key);
                }

            });
            //3.获取核心数据
            //兼容List<Long>形式标记
            boolean isCollection = false;
            Collection<Object> values = idKeyMap.values();
            Object firstValue = idKeyMap.values().iterator().next();
            if (Collection.class.isAssignableFrom(firstValue.getClass())) {
                isCollection = true;
                values = values.stream().map(val -> (Collection<?>) val).flatMap(Collection::stream).distinct().collect(Collectors.toList());
            }


            //4.转换赋值
            for (Field targetField : targetClassFieldMap.keySet()) {
                Class<?> aClass = targetClassFieldMap.get(targetField);
                Map<Object, Object> dataMap = autoWrapServiceMap.get(aClass).autoWrap(values);
                for (T target : targetList) {
                    if (MapUtil.isEmpty(dataMap)) {
                        continue;
                    }
                    if (!isCollection) {
                        Object o = dataMap.get(idKeyMap.get(target.getId()));
                        if (Objects.nonNull(o)) {
                            handleAbstractNameDTOList(Collections.singletonList(o));
                        }
                        MappingProvider.setTargetField(target, targetField, o);
                    } else {
                        //Collection兼容
                        Object val = idKeyMap.get(target.getId());
                        Collection<?> keyCollection = (Collection<?>) val;
                        if (CollUtil.isEmpty(keyCollection)) {
                            continue;
                        }
                        List<Object> realDate = keyCollection.stream().map(dataMap::get).filter(Objects::nonNull).collect(Collectors.toList());
                        handleAbstractNameDTOList(realDate);
                        MappingProvider.setTargetField(target, targetField, realDate);
                    }

                }

            }

        }
    }

    public static void afterAll(@MappingTarget Object object) {
        if (Objects.isNull(object)) {
            return;
        }
        if (List.class.isAssignableFrom(object.getClass())) {
            handleAbstractNameDTOList(GenericsUtil.toList(object));
        } else {
            handleAbstractNameDTOList(Collections.singletonList(object));
        }
    }


    static <T extends BaseNameDTO> void handleAbstractNameDTOList(@MappingTarget List<Object> objects) {
        if (CollUtil.isEmpty(objects)) {
            return;
        }
        if (BaseNameDTO.class.isAssignableFrom(objects.iterator().next().getClass())) {
            doHandleAbstractNameDTOList(GenericsUtil.toList(objects));
        }
    }

    //后续可沉淀为策略

    static <T extends BaseNameDTO> void doHandleAbstractNameDTOList(@MappingTarget List<T> abstractNameDTOList) {
        List<Long> creatorIdList = CollectionUtils.convertList(abstractNameDTOList, BaseNameDTO::getCreator);
        List<Long> updaterIdList = CollectionUtils.convertList(abstractNameDTOList, BaseNameDTO::getUpdater);
        Collection<Long> userIdList = CollUtil.addAllIfNotContains(creatorIdList, updaterIdList);
        if (CollUtil.isEmpty(userIdList)) {
            return;
        }
        UserNameTranslation userNameTranslation = SpringContextHelper.getBean(UserNameTranslation.class);
        Map<Long, String> userNameMap = userNameTranslation.getUserNameMap(userIdList);
        if (CollUtil.isEmpty(userNameMap)) {
            return;
        }
        abstractNameDTOList.forEach(abstractNameDTO -> {
            abstractNameDTO.setCreatorName(userNameMap.get(abstractNameDTO.getCreator()));
            abstractNameDTO.setUpdaterName(userNameMap.get(abstractNameDTO.getUpdater()));

        });

    }

    /**
     * 解析AutoWrapper
     */

    public static Map<Class<?>, AutoWrapper> getAbstractAutoWrapService(Map<String, AutoWrapper> autoWrapServiceMap) {
        if (CollUtil.isEmpty(autoWrapServiceMap)) {
            return Collections.emptyMap();
        }
        //这里的key是第2个泛型类型,Field为AutoWrapper属性
        return autoWrapServiceMap.values().stream()
                .collect(Collectors.toMap(wrapService -> GenericTypeUtils.resolveTypeArguments(wrapService.getClass(), AutoWrapService.class)[1], Function.identity()
                        , (a, b) -> {
                            throw new FrameworkException("AutoWrapper中,一个自动装载类型只能有一个wrapper实现,当前实体属性类型:" + a.getClass() + ",重复定义,请检查:" + b.getClass());
                        }));
    }

    /**
     * 使用递归获某个类及其所有父类所有字段
     *
     * @param clazz
     * @return
     */
    private static Field[] getAllFields(Class<?> clazz, Class<?> stopClass) {
        // 使用递归获取所有字段
        if (clazz == null) {
            return null;
        }

        // 获取当前类声明的字段
        Field[] declaredFields = clazz.getDeclaredFields();
        if (clazz.equals(stopClass)) {
            return declaredFields;
        }
        // 获取父类的字段
        Field[] parentFields = getAllFields(clazz.getSuperclass(), stopClass);
        if (ArrayUtils.isEmpty(parentFields)) {
            return null;
        }
        // 合并当前类和父类的字段
        Field[] allFields = new Field[declaredFields.length + parentFields.length];
        System.arraycopy(declaredFields, 0, allFields, 0, declaredFields.length);
        System.arraycopy(parentFields, 0, allFields, declaredFields.length, parentFields.length);

        return allFields;
    }

    public static <T extends BaseIdDO<Long>> List<Field> getBMappingField(T source) {
        Field[] fields = source.getClass().getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> Objects.nonNull(field.getAnnotation(AutoWrap.class)))
                .collect(Collectors.toList());
    }

    public static <S extends BaseIdDO<Long>, M> Field getFieldByClass(S target, Class<M> mClass) {
        Field[] fields = target.getClass().getDeclaredFields();
        List<Field> fieldList = Arrays.stream(fields)
                .filter(field -> {
                    if (field.getType().equals(mClass)) {
                        return true;
                    }
                    if (field.getType().equals(List.class)) {
                        return getGenericClass(field, 0).equals(mClass);
                    }
                    //暂时不支持Map
                    return false;
                }).collect(Collectors.toList());
        if (CollUtil.isEmpty(fieldList)) {
            return null;
        }
        Assert.isTrue(fieldList.size() == 1, "目标中存在多个标注为特定类型的属性");
        return fieldList.get(0);
    }

    private static Class<?> getGenericClass(Field field, int index) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length == 0) {
                return null;
            }
            return (Class<?>) typeArguments[index];
        }
        return null;
    }


    /**
     * 为目标中标注为特定类型的属性赋值
     *
     * @param target
     * @param <S>
     * @param <M>
     * @return
     */

    public static <S extends BaseIdDO<Long>, M> void setTargetField(S target, Field targetField, M value) {
        if (Objects.isNull(targetField) || Objects.isNull(value)) {
            return;
        }
        if (!targetField.getType().isAssignableFrom(value.getClass())) {
            //给定的类型不匹配
            throw new RuntimeException("类型不匹配,无法转换");
        }

        targetField.setAccessible(Boolean.TRUE);
        try {
            targetField.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
