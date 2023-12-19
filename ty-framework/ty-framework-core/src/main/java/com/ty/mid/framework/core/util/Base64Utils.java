package com.ty.mid.framework.core.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.common.constant.FrameworkConstant;
import com.ty.mid.framework.common.lang.NonNull;
import com.ty.mid.framework.common.lang.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * Base64 工具类
 */
@Slf4j
@ThreadSafe
public abstract class Base64Utils {

    public static String encode(@NonNull String source) {
        return encode(source.getBytes(FrameworkConstant.Encode.CHARSET_UTF_8));
    }

    public static String encode(@NonNull String source, @NonNull Charset charset) {
        return encode(source.getBytes(charset));
    }

    public static String encode(@NonNull byte[] sources) {
        return Base64.encode(sources);
    }

    public static String decode(@NonNull String source) {
        return Base64.decodeStr(source);
    }

    public static String decode(@NonNull byte[] sources) {
        return StrUtil.str(Base64.decode(sources), FrameworkConstant.Encode.CHARSET_UTF_8);
    }

}
