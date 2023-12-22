//package com.ty.mid.framework.common.pojo;
//
//public interface KVEnumConverter<K,V> extends KVBaseResp<K,V> {
//
//    @SuppressWarnings("unchecked")
//    default KVBaseResp<K, V> covert2KValueResp(){
//        KVBaseResp<?, ?> build = KVHelper.buildWithCache(getKey(), getValue(), this);
//        return (KVBaseResp<K, V>) build;
//    }
//
//}
