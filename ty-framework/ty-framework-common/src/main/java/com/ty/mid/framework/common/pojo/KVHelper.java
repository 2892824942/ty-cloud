//package com.ty.mid.framework.common.pojo;
//
//
//import com.google.common.collect.Maps;
//
//import java.util.Map;
//import java.util.Objects;
//import java.util.function.Function;
//
///**
// * 键值对
// */
//public class KVHelper {
//    private static final Map<String, KVBaseResp<Object,Object>> KEY_VALUE_MAP = Maps.newHashMap();
//
//    public static <R extends KVBaseResp<?,?>> KVBaseResp<?,?> buildWithCache(Object key, Object value,R resource) {
//        String innerKey = resource.getClass().getSimpleName().concat(":").concat(Objects.toString(key));
//
//        return KEY_VALUE_MAP.computeIfAbsent(innerKey, kvBaseRespFunction(key, value));
//    }
//
//
//    public static  KVBaseResp<?,?> build(Object key, Object value) {
//        return kvBaseRespFunction(key, value).apply("normal");
//    }
//    private static
//    Function<? super String, ? extends KVBaseResp<Object, Object>>  kvBaseRespFunction(Object key, Object value){
//        return a -> new KVBaseResp<Object, Object>() {
//            @Override
//            public Object getKey() {
//                return key;
//            }
//
//            @Override
//            public Object getValue() {
//                return value;
//            }
//        };
//    }
//
//}
