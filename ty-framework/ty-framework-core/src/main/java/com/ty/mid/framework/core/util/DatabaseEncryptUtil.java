package com.ty.mid.framework.core.util;


import com.ty.mid.framework.common.constant.EncryptConstant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * description: DatabaseEncryptUtil
 * date: 2021/7/14 09:48
 * author: wuqiuhang
 */
public class DatabaseEncryptUtil {

    private static final String ENCRYPT_PREFIX = "$:#";
    private static List<String> ENCRYPT_TYPE_NO = new ArrayList<>();

    static {
        for (int i = 0; i < 10; i++) {
            ENCRYPT_TYPE_NO.add(String.valueOf(i));
        }
    }


    public static String encryptRC4(String sourceValue, String key) {
        if (StringUtils.isEmpty(sourceValue)) {
            return sourceValue;
        }
        String encryptRC4 = EncryptUtils.encryptRC4(sourceValue, key);
        return EncryptConstant.EncryptType.RC4.concat("$:#").concat(encryptRC4);
    }

    public static String decryptRC4(String encryptValue, String key) {
        if (StringUtils.isEmpty(encryptValue) || !isEncryptRC4(encryptValue)) {
            return encryptValue;
        }
        String encryptRC4 = encryptValue.substring(4);
        return EncryptUtils.decryptRC4(encryptRC4, key);
    }


    public static List<String> encryptRC4List(String sourceValues, String key) {
        if (StringUtils.isEmpty(sourceValues)) {
            return new ArrayList<>();
        }
        Set<String> values = StringUtils.commaDelimitedListToSet(sourceValues);

        return encryptRC4List(values, key);
    }

    public static List<String> encryptRC4List(Collection<String> sourceValues, String key) {
        if (CollectionUtils.isEmpty(sourceValues)) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        for (String value : sourceValues) {
            result.add(encryptRC4(value, key));
        }
        return result;
    }

    public static List<String> decryptRC4List(String encryptValues, String key) {
        if (StringUtils.isEmpty(encryptValues)) {
            return new ArrayList<>();
        }
        Set<String> encrypts = StringUtils.commaDelimitedListToSet(encryptValues);

        return decryptRC4List(encrypts, key);
    }

    public static List<String> decryptRC4List(Collection<String> encryptValues, String key) {
        if (CollectionUtils.isEmpty(encryptValues)) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        for (String value : encryptValues) {
            result.add(decryptRC4(value, key));
        }
        return result;
    }


    public static boolean isEncryptRC4(String text) {

        if (StringUtils.isEmpty(text) || text.length() < 5) {
            return false;
        }
        String encryptType = text.substring(0, 1);
        String prefix = text.substring(1, 4);
        return ENCRYPT_TYPE_NO.contains(encryptType) && ENCRYPT_PREFIX.equals(prefix);

//        text = text.substring(0, 4);
//        String pattern = "(^[A-Z0-9])+(\\$:#)";
//        return text.matches(pattern);
    }


}
