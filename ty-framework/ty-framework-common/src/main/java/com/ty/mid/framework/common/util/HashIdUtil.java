package com.ty.mid.framework.common.util;


import cn.hutool.core.codec.Hashids;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.ty.mid.framework.common.exception.FrameworkException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class HashIdUtil {

    public static final int MIN_HASH_LEN = 12;

    public static String encode(Long number, String salt, int minLen) {
        Assert.notNull(number, "数字不能为空");
        Assert.notEmpty(salt, "salt 不能为空");

        //优化hashid传入id为负数报错的问题,此情况转成正数
        String prefix = "";
        if (number < 0) {
            prefix = "-";
            number = -number;
        }
        Hashids hashids = Hashids.create(salt.toCharArray(), minLen);

        return prefix.concat(hashids.encode(number));
    }

    public static String encode(Long number, String salt) {
        return encode(number, salt, MIN_HASH_LEN);
    }

    public static Long decode(String id, String salt, int minLen) {

        Assert.notEmpty(id, "id不能为空");
        Assert.notEmpty(salt, "salt 不能为空");

        //优化hashid传入id为负数报错的问题,此情况转成正数
        long prefix = 1L;
        if (id.startsWith("-")) {
            id = id.substring(1);
            prefix = -1L;
        }
        Assert.isTrue(id.length() >= minLen, "数据id格式错误");
        Hashids hashids = Hashids.create(salt.toCharArray(), minLen);
        long[] decode = hashids.decode(id);

        Assert.isTrue(ObjectUtil.isNotEmpty(decode), "数据id格式错误");
        Assert.isTrue(decode.length == 1, "数据id格式错误");
        return prefix * decode[0];
    }

    public static Object decodeString(String paramVal, String paramName, String salt, int minLen) {
        if (StringUtils.isEmpty(paramVal)) {
            log.warn("resolve origin id for [p:{} ,v:{}] fail, because param value is null or empty!", paramName, paramVal);
            return null;
        }

        //兼容多个加密id使用","隔开的情况
        if (paramVal.contains(",") && paramVal.split(",").length >= 1) {
            StringJoiner stringJoiner = new StringJoiner(",");
            String[] split = paramVal.split(",");
            for (String s : split) {
                if (StringUtils.isEmpty(s)) {
                    stringJoiner.add("");
                } else {
                    try {
                        stringJoiner.add(String.valueOf(HashIdUtil.decode(s, salt, minLen)));
                    } catch (Exception e) {
                        log.warn("resolve origin id for [p:{} ,v:{}] fail, because of param value decode error!", paramName, paramVal, e);
                        throw new FrameworkException("加解密异常[HashedId]");
                    }
                }
            }
            return stringJoiner.toString();
        }
        //单纯的字符串
        return String.valueOf(HashIdUtil.decode(paramVal, salt, minLen));
    }

    public static Object decodeCollection(List<?> paramVal, String paramName, Class<? extends Collection<Long>> paramClass, String salt, int minLen) {
        if (CollUtil.isEmpty(paramVal)) {
            return Collections.emptyList();
        }

        try {
            Collection<Long> collection;
            if (TreeSet.class.isAssignableFrom(paramClass)) {
                collection = new TreeSet<>();
            } else if (Set.class.isAssignableFrom(paramClass)) {
                collection = new HashSet<>(paramVal.size() * 2);
            } else if (LinkedList.class.isAssignableFrom(paramClass)) {
                collection = new LinkedList<>();
            } else if (List.class.isAssignableFrom(paramClass)) {
                collection = new ArrayList<>();
            } else {
                throw new FrameworkException("仅支持常见的Set及List加解密[HashedId]");
            }

            paramVal.forEach(data -> {
                if (Objects.isNull(data)) {
                    collection.add(null);
                }

                collection.add(HashIdUtil.decode(data.toString(), salt, minLen));
            });
            return collection;
        } catch (Exception e) {
            log.warn("resolve origin id for [p:{} ,v:{}] fail, because of param value decode error!", paramName, paramVal, e);
            throw new FrameworkException("加解密异常[HashedId]");
        }
    }

}
