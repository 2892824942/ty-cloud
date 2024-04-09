package com.ty.mid.framework.core.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.MD5;
import com.ty.mid.framework.common.constant.FrameworkConstant;
import com.ty.mid.framework.common.lang.NonNull;
import com.ty.mid.framework.common.lang.Nullable;
import com.ty.mid.framework.common.lang.ThreadSafe;

/**
 * Hash 工具类
 */
@ThreadSafe
public class HashUtils {

    public static String md5Hash(@NonNull String source) {
        return md5Hash(source, null);
    }

    public static String md5Hash(@NonNull String source, @Nullable String salt) {
        MD5 md5 = StrUtil.isEmpty(salt) ? MD5.create() : new MD5(getByte(salt));
        return md5.digestHex(source);
    }

    public static String sha256Hash(@NonNull String source) {
        return sha256Hash(source, null);
    }

    public static String sha256Hash(@NonNull String source, @Nullable String salt) {
        Digester digester = SecureUtil.sha256();
        return digester.setSalt(getByte(salt)).digestHex(source);
    }

    private static byte[] getByte(@NonNull String str) {
        return str.getBytes(FrameworkConstant.Encode.CHARSET_UTF_8);
    }
}
