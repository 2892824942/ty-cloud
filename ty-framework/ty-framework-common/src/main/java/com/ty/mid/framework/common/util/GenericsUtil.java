/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.ty.mid.framework.common.util;


import com.ty.mid.framework.common.lang.ThreadSafe;
import com.ty.mid.framework.common.util.collection.MiscUtils;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 泛型工具类
 */
@ThreadSafe
public final class GenericsUtil {

    public static final String module = MiscUtils.class.getName();

    private GenericsUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <V> V cast(Object object) {
        return (V) object;
    }

    public static <V> Class<V> cast2Class(Class<?> classObj) {
        return cast(classObj);
    }

    private static <C extends Collection<?>> C cast2Collection(Object object, Class<C> clz) {
        return clz.cast(object);
    }

    public static <C extends Collection<?>> void checkCollectionContainment(Object object, Class<C> clz, Class<?> type) {
        if (object != null) {
            if (!(clz.isInstance(object))) throw new ClassCastException("Not a " + clz.getName());
            int i = 0;
            for (Object value : (Collection<?>) object) {
                if (value != null && !type.isInstance(value)) {
                    throw new IllegalArgumentException("Value(" + i + "), with value(" + value + ") is not a " + type.getName());
                }
                i++;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> check2Collection(Object object) {
        return (Collection<T>) cast2Collection(object, Collection.class);
    }

    public static <T> Collection<T> check2Collection(Object object, Class<T> type) {
        checkCollectionContainment(object, Collection.class, type);
        return check2Collection(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> check2List(Object object) {
        return (List<T>) cast2Collection(object, List.class);
    }

    public static <T> List<T> check2List(Object object, Class<T> type) {
        checkCollectionContainment(object, List.class, type);
        return check2List(object);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> check2Map(Object object) {
        if (object != null && !(object instanceof Map)) {
            throw new ClassCastException("Not a map");
        }
        return (Map<K, V>) object;
    }

    public static <K, V> Map<K, V> check2Map(Object object, Class<K> keyType, Class<V> valueType) {
        if (object != null) {
            if (!(object instanceof Map<?, ?>)) {
                throw new ClassCastException("Not a map");
            }
            Map<?, ?> map = (Map<?, ?>) object;
            int i = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null && !keyType.isInstance(entry.getKey())) {
                    throw new IllegalArgumentException("Key(" + i + "), with value(" + entry.getKey() + ") is not a " + keyType);
                }
                if (entry.getValue() != null && !valueType.isInstance(entry.getValue())) {
                    throw new IllegalArgumentException("Value(" + i + "), with value(" + entry.getValue() + ") is not a " + valueType);
                }
                i++;
            }
        }
        return check2Map(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> Stack<T> check2Stack(Object object) {
        return (Stack<T>) cast2Collection(object, Stack.class);
    }

    public static <T> Stack<T> check2Stack(Object object, Class<T> type) {
        checkCollectionContainment(object, Stack.class, type);
        return check2Stack(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> check2Set(Object object) {
        return (Set<T>) cast2Collection(object, Set.class);
    }

    public static <T> Set<T> check2Set(Object object, Class<T> type) {
        checkCollectionContainment(object, Set.class, type);
        return check2Set(object);
    }

    /**
     * Returns the Object argument as a parameterized List if the Object argument
     * is an instance of List. Otherwise returns null.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(Object object) {
        if (object != null && !(object instanceof List)) {
            return null;
        }
        return (List<T>) object;
    }

    /**
     * Returns the Object argument as a parameterized Map if the Object argument
     * is an instance of Map. Otherwise returns null.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(Object object) {
        if (object != null && !(object instanceof Map)) {
            return null;
        }
        return (Map<K, V>) object;
    }

    public static <K, V> Map<K, V> toMap(Class<K> keyType, Class<V> valueType, Object... data) {
        if (data == null) {
            return null;
        }
        if (data.length % 2 == 1) {
            throw new IllegalArgumentException("You must pass an even sized array to the toMap method");
        }
        Map<K, V> map = new LinkedHashMap<K, V>();
        for (int i = 0; i < data.length; ) {
            Object key = data[i];
            if (key != null && !(keyType.isInstance(key)))
                throw new IllegalArgumentException("Key(" + i + ") is not a " + keyType.getName() + ", was(" + key.getClass().getName() + ")");
            i++;
            Object value = data[i];
            if (value != null && !(valueType.isInstance(value)))
                throw new IllegalArgumentException("Value(" + i + ") is not a " + keyType.getName() + ", was(" + key.getClass().getName() + ")");
            i++;
            map.put(keyType.cast(key), valueType.cast(value));
        }
        return map;
    }

    @SuppressWarnings("hiding")
    public static <K, Object> Map<K, Object> toMap(Class<K> keyType, Object... data) {
        if (data == null) {
            return null;
        }
        if (data.length % 2 == 1) {
            throw new IllegalArgumentException("You must pass an even sized array to the toMap method");
        }
        Map<K, Object> map = new LinkedHashMap<K, Object>();
        for (int i = 0; i < data.length; ) {
            Object key = data[i];
            if (key != null && !(keyType.isInstance(key)))
                throw new IllegalArgumentException("Key(" + i + ") is not a " + keyType.getName() + ", was(" + key.getClass().getName() + ")");
            i++;
            Object value = data[i];
            map.put(keyType.cast(key), value);
        }
        return map;
    }

    /**
     * 此方法只用作已知的,确定的泛型定义中可识别,对于通用拉取的,泛型本就不确定的无法识别
     *
     * @param targetClass
     * @param index
     * @param <T>
     * @return
     */
    public static <T> Class<T> getGenericTypeByIndex(Class<?> targetClass, int index) {
        Type superclass = targetClass.getGenericSuperclass();

        if (superclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superclass;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length > 0 && typeArguments[index] instanceof Class) {
                return (Class<T>) typeArguments[index];
            }
        }

        throw new IllegalStateException("Unable to determine the generic type.");
    }

    public static List<Class<?>> getGenericTypeList(Class<?> genericClass) {
        Type rawType = genericClass.getGenericSuperclass();
        if (rawType instanceof Class<?>) {
            return Collections.emptyList();
        }

        ParameterizedType o = (ParameterizedType) rawType;

        Type[] typeArguments = o.getActualTypeArguments();
        List<Class<?>> typeArgumentClassList = new ArrayList<Class<?>>();

        for (Type typeArgument : typeArguments) {
            typeArgumentClassList.add(getGenericType(typeArgument));
        }

        return typeArgumentClassList;
    }

    public static Class<?> getGenericType(Type typeArgument) {
        if (typeArgument instanceof Class<?>) {
            return (Class<?>) typeArgument;
        } else if (typeArgument instanceof ParameterizedType) {
            Type innerType = ((ParameterizedType) typeArgument).getRawType();
            return getGenericType(innerType);
        } else if (typeArgument instanceof GenericArrayType) {
            Type compType = ((GenericArrayType) typeArgument).getGenericComponentType();
            Class<?> compClazz = getGenericType(compType);

            if (compClazz != null) {
                return Array.newInstance(compClazz, 0).getClass();
            }
        }

        return null;
    }

}
