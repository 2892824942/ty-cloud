package com.ty.mid.framework.common.util;


import cn.hutool.core.codec.Hashids;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;

public class HashIdUtil {

    public static final int MIN_HASH_LEN = 12;

    public static String encode(Long number, String salt, int minLen) {
        Assert.notNull(number, "数字不能为空");
        Assert.notEmpty(salt, "salt 不能为空");
        Hashids hashids = Hashids.create(salt.toCharArray(), minLen);

        return hashids.encode(number);
    }

    public static String encode(Long number, String salt) {
        return encode(number, salt, MIN_HASH_LEN);
    }

    public static Long decode(String id, String salt, int minLen) {
        Assert.notEmpty(id, "id不能为空");
        Assert.notEmpty(salt, "salt 不能为空");
        Assert.isTrue(id.length() >= minLen, "数据id格式错误");
        Hashids hashids = Hashids.create(salt.toCharArray(), minLen);
        long[] decode = hashids.decode(id);

        Assert.isTrue(ObjectUtil.isNotEmpty(decode), "数据id格式错误");
        Assert.isTrue(decode.length == 1, "数据id格式错误");
        return decode[0];
    }
}
