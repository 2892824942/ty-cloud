package com.ty.mid.framework.core.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.ty.mid.framework.common.constant.FrameworkConstant;
import com.ty.mid.framework.common.lang.NonNull;
import com.ty.mid.framework.common.lang.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

/**
 * AES 加密工具类 <p>
 * 注意：aesKey必须是16位 
 */
@Slf4j
@ThreadSafe
public class AesUtils {

    public static String encrypt(@NonNull String content, @NonNull String aesKey) {
        AES aes = SecureUtil.aes(toBytes(aesKey));
        byte[] contentBytes = toBytes(content);
        return aes.encryptHex(contentBytes);

    }

    public static byte[] encrypt(@NonNull byte[] content, @NonNull String aesKey) {
        AES aes = SecureUtil.aes(toBytes(aesKey));
        return aes.encrypt(content);
    }

    public static String decrypt(@NonNull String cipherText, @NonNull String aesKey) {
        AES aes = SecureUtil.aes(toBytes(aesKey));
        return aes.decryptStr(cipherText);
    }

    public static byte[] decrypt(@NonNull byte[] cipherContent, @NonNull String aesKey) {
        AES aes = SecureUtil.aes(toBytes(aesKey));
        return aes.decrypt(cipherContent);
    }

    private static byte[] toBytes(@NonNull String str) {
        return str.getBytes(FrameworkConstant.Encode.CHARSET_UTF_8);
    }


}
