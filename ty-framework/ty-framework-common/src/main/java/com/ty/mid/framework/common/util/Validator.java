package com.ty.mid.framework.common.util;

import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.lang.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 参数校验工具类
 */
@Slf4j
@ThreadSafe
public class Validator {

    public static final String DEFAULT_ERROR_CODE = "500";

    public static String DEFAULT_MSG_REQUIRE_NON_NULL = "参数 %s 不能为空！";
    public static String DEFAULT_MSG_REQUIRE_NULL = "参数 %s 必须为空！";
    public static String DEFAULT_MSG_REQUIRE_NON_EMPTY = "参数 %s 必须为空！";
    public static String DEFAULT_MSG_REQUIRE_EMPTY = "参数 %s 必须为空！";
    public static String DEFAULT_MSG_REQUIRE_TRUE = "参数 %s 必须为 True！";
    public static String DEFAULT_MSG_REQUIRE_FALSE = "参数 %s 必须为 False！";
    public static String DEFAULT_MSG_REQUIRE_EQUALS = "参数 %s 和参数 %s 必须相等";

    public static int DEFAULT_PRICE_SCALE = 2;
    public static RoundingMode DEFAULT_PRICE_ROUNDING_MODE = RoundingMode.HALF_EVEN;

    // non null check
    public static void requireNonNull(byte[] val, String errorMessage) {
        requireNonNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNonNull(byte[] val, String errorCode, String errorMessage) {
        if (null == val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNonNull(String val, String errorMessage) {
        requireNonNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNonNull(String val, String errorCode, String errorMessage) {
        // log.info("checking nonNull, value: {}", val);
        if (null == val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNonNull(Integer val, String errorMessage) {
        requireNonNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNonNull(Integer val, String errorCode, String errorMessage) {
        // log.info("checking nonNull, value: {}", val);
        if (null == val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNonNull(Long val, String errorMessage) {
        requireNonNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNonNull(Long val, String errorCode, String errorMessage) {
        // log.info("checking nonNull, value: {}", val);
        if (null == val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNonNull(Double val, String errorMessage) {
        requireNonNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNonNull(Double val, String errorCode, String errorMessage) {
        // log.info("checking nonNull, value: {}", val);
        if (null == val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNonNull(Float val, String errorMessage) {
        requireNonNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNonNull(Float val, String errorCode, String errorMessage) {
        // log.info("checking nonNull, value: {}", val);
        if (null == val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNonNull(BigDecimal val, String errorMessage) {
        requireNonNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNonNull(BigDecimal val, String errorCode, String errorMessage) {
        // log.info("checking nonNull, value: {}", val);
        if (null == val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNonNull(Date val, String errorMessage) {
        requireNonNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNonNull(Date val, String errorCode, String errorMessage) {
        // log.info("checking nonNull, value: {}", val);
        if (null == val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNonNull(Object val, String errorMessage) {
        requireNonNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNonNull(Object val, String errorCode, String errorMessage) {
        // log.info("checking nonNull, value: {}", val);
        if (null == val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    // null check

    public static void requireNull(String val, String errorMessage) {
        requireNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNull(String val, String errorCode, String errorMessage) {
        // log.info("checking null, value: {}", val);
        if (null != val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNull(Integer val, String errorMessage) {
        requireNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNull(Integer val, String errorCode, String errorMessage) {
        // log.info("checking null, value: {}", val);
        if (null != val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNull(Long val, String errorMessage) {
        requireNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNull(Long val, String errorCode, String errorMessage) {
        // log.info("checking null, value: {}", val);
        if (null != val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNull(Double val, String errorMessage) {
        requireNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNull(Double val, String errorCode, String errorMessage) {
        // log.info("checking null, value: {}", val);
        if (null != val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNull(Float val, String errorMessage) {
        requireNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNull(Float val, String errorCode, String errorMessage) {
        // log.info("checking null, value: {}", val);
        if (null != val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNull(BigDecimal val, String errorMessage) {
        requireNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNull(BigDecimal val, String errorCode, String errorMessage) {
        // log.info("checking null, value: {}", val);
        if (null != val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNull(Date val, String errorMessage) {
        requireNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNull(Date val, String errorCode, String errorMessage) {
        // log.info("checking null, value: {}", val);
        if (null != val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireNull(Object val, String errorMessage) {
        requireNull(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNull(Object val, String errorCode, String errorMessage) {
        // log.info("checking null, value: {}", val);
        if (null != val) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    // non empty check

    public static void requireNonEmpty(String val, String message) {
        requireNonEmpty(val, DEFAULT_ERROR_CODE, message);
    }

    public static void requireNonEmpty(String val, String code, String errorMessage) {
        // log.info("checking nonEmpty, value: {}", val);
        if (StringUtils.isEmpty(val)) {
            throw new FrameworkException(code, errorMessage);
        }
    }

    public static void requireNonEmpty(List<?> val, String message) {
        requireNonEmpty(val, DEFAULT_ERROR_CODE, message);
    }

    public static void requireNonEmpty(List<?> val, String code, String errorMessage) {
        // log.info("checking nonEmpty, value: {}", val);
        if (CollectionUtils.isEmpty(val)) {
            throw new FrameworkException(code, errorMessage);
        }
    }

    public static void requireNonEmpty(Set<?> val, String message) {
        requireNonEmpty(val, DEFAULT_ERROR_CODE, message);
    }

    public static void requireNonEmpty(Set<?> val, String code, String errorMessage) {
        // log.info("checking nonEmpty, value: {}", val);
        if (CollectionUtils.isEmpty(val)) {
            throw new FrameworkException(code, errorMessage);
        }
    }

    public static void requireNonEmpty(Collection<?> val, String message) {
        requireNonEmpty(val, DEFAULT_ERROR_CODE, message);
    }

    public static void requireNonEmpty(Collection<?> val, String code, String errorMessage) {
        // log.info("checking nonEmpty, value: {}", val);
        if (CollectionUtils.isEmpty(val)) {
            throw new FrameworkException(code, errorMessage);
        }
    }

    public static void requireNonEmpty(Map<?, ?> val, String message) {
        requireNonEmpty(val, DEFAULT_ERROR_CODE, message);
    }

    public static void requireNonEmpty(Map<?, ?> val, String code, String errorMessage) {
        // log.info("checking nonEmpty, value: {}", val);
        if (MapUtils.isEmpty(val)) {
            throw new FrameworkException(code, errorMessage);
        }
    }

    // empty check
    public static void requireEmpty(String val, String message) {
        requireEmpty(val, DEFAULT_ERROR_CODE, message);
    }

    public static void requireEmpty(String val, String code, String errorMessage) {
        // log.info("checking empty, value: {}", val);
        if (!StringUtils.isEmpty(val)) {
            throw new FrameworkException(code, errorMessage);
        }
    }

    public static void requireEmpty(List<?> val, String message) {
        requireEmpty(val, DEFAULT_ERROR_CODE, message);
    }

    public static void requireEmpty(List<?> val, String code, String errorMessage) {
        // log.info("checking empty, value: {}", val);
        if (!CollectionUtils.isEmpty(val)) {
            throw new FrameworkException(code, errorMessage);
        }
    }

    public static void requireEmpty(Set<?> val, String message) {
        requireEmpty(val, DEFAULT_ERROR_CODE, message);
    }

    public static void requireEmpty(Set<?> val, String code, String errorMessage) {
        // log.info("checking empty, value: {}", val);
        if (!CollectionUtils.isEmpty(val)) {
            throw new FrameworkException(code, errorMessage);
        }
    }

    public static void requireEmpty(Collection<?> val, String message) {
        requireEmpty(val, null, message);
    }

    public static void requireEmpty(Collection<?> val, String code, String errorMessage) {
        // log.info("checking empty, value: {}", val);
        if (!CollectionUtils.isEmpty(val)) {
            throw new FrameworkException(code, errorMessage);
        }
    }

    public static void requireEmpty(Map<?, ?> val, String message) {
        requireEmpty(val, null, message);
    }

    public static void requireEmpty(Map<?, ?> val, String code, String errorMessage) {
        // log.info("checking empty, value: {}", val);
        if (!MapUtils.isEmpty(val)) {
            throw new FrameworkException(code, errorMessage);
        }
    }

    public static void requireNonEmpty(byte[] val, String errorMessage) {
        requireNonEmpty(val, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireNonEmpty(byte[] val, String errorCode, String errorMessage) {
        if (null == val || val.length == 0) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    // require true

    public static void requireTrue(final boolean expression, String errorMessage) {
        requireTrue(expression, null, errorMessage);
    }

    public static void requireTrue(final boolean expression, String errorCode, String errorMessage) {
        // log.info("checking true, value: {}", expression);
        if (!expression) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    // require false

    public static void requireFalse(final boolean expression, String errorMessage) {
        requireFalse(expression, null, errorMessage);
    }

    public static void requireFalse(final boolean expression, String errorCode, String errorMessage) {
        // log.info("checking false, value: {}", expression);
        if (expression) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    // require equals
    public static void requireEquals(String a, String b, String errorMessage) {
        requireEquals(a, b, null, errorMessage);
    }

    public static void requireEquals(String a, String b, String errorCode, String errorMessage) {
        // log.info("checking equals, a: {}, b: {}", a, b);
        requireNonNull(a, formatNonNullMessage("a"));
        requireNonNull(b, formatNonNullMessage("b"));

        if (!a.equals(b)) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireEquals(Long a, Long b, String errorMessage) {
        requireEquals(a, b, DEFAULT_ERROR_CODE, errorMessage);
    }

    public static void requireEquals(Long a, Long b, String errorCode, String errorMessage) {
        // log.info("checking equals, a: {}, b: {}", a, b);
        requireNonNull(a, formatNonNullMessage("a"));
        requireNonNull(b, formatNonNullMessage("b"));

        if (!a.equals(b)) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireEquals(Integer a, Integer b, String errorMessage) {
        requireEquals(a, b, null, errorMessage);
    }

    public static void requireEquals(Integer a, Integer b, String errorCode, String errorMessage) {
        // log.info("checking equals, a: {}, b: {}", a, b);
        requireNonNull(a, formatNonNullMessage("a"));
        requireNonNull(b, formatNonNullMessage("b"));

        if (!a.equals(b)) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireEquals(Double a, Double b, String errorMessage) {
        requireEquals(a, b, null, errorMessage);
    }

    public static void requireEquals(Double a, Double b, String errorCode, String errorMessage) {
        // log.info("checking equals, a: {}, b: {}", a, b);
        requireNonNull(a, formatNonNullMessage("a"));
        requireNonNull(b, formatNonNullMessage("b"));

        if (!a.equals(b)) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireEquals(BigDecimal a, BigDecimal b, String errorMessage) {
        requireEquals(a, b, null, errorMessage);
    }

    public static void requireEquals(BigDecimal a, BigDecimal b, String errorCode, String errorMessage) {
        // log.info("checking equals, a: {}, b: {}", a, b);
        requireNonNull(a, formatNonNullMessage("a"));
        requireNonNull(b, formatNonNullMessage("b"));

        if (!a.equals(b)) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requirePriceEquals(BigDecimal a, BigDecimal b, String errorMessage) {
        requireEquals(a, b, null, errorMessage);
    }

    public static void requirePriceEquals(BigDecimal a, BigDecimal b, String errorCode, String errorMessage) {
        // log.info("checking priceEquals, a: {}, b: {}", a, b);
        requireNonNull(a, formatNonNullMessage("a"));
        requireNonNull(b, formatNonNullMessage("b"));

        if (fixPrice(a).compareTo(fixPrice(b)) != 0) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requirePriceEquals(BigDecimal a, BigDecimal b, int scale, String errorMessage) {
        requirePriceEquals(a, b, scale, null, errorMessage);
    }

    public static void requirePriceEquals(BigDecimal a, BigDecimal b, int scale, String errorCode, String errorMessage) {
        //  log.info("checking priceEquals, a: {}, b: {}, scale: {}", a, b, scale);
        requireNonNull(a, formatNonNullMessage("a"));
        requireNonNull(b, formatNonNullMessage("b"));

        if (fixPrice(a, scale).compareTo(fixPrice(b, scale)) != 0) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireEquals(Object a, Object b, String errorMessage) {
        requireEquals(a, b, null, errorMessage);
    }

    public static void requireEquals(Object a, Object b, String errorCode, String errorMessage) {
        // log.info("checking equals, a: {}, b: {}", a, b);
        requireNonNull(a, formatNonNullMessage("a"));
        requireNonNull(b, formatNonNullMessage("b"));

        if (!a.equals(b)) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireIn(String object, Set<String> in, String errorMessage) {
        requireIn(object, in, null, errorMessage);
    }

    public static void requireIn(String object, Set<String> in, String errorCode, String errorMessage) {
        requireNonEmpty(object, formatNonNullMessage("object"));
        requireNonEmpty(in, formatNonNullMessage("in"));

        if (!in.contains(object)) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireIn(Long object, Set<Long> in, String errorMessage) {
        requireIn(object, in, null, errorMessage);
    }

    public static void requireIn(Long object, Set<Long> in, String errorCode, String errorMessage) {
        requireNonNull(object, formatNonNullMessage("object"));
        requireNonEmpty(in, formatNonNullMessage("in"));

        if (!in.contains(object)) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    public static void requireIn(Integer object, Set<Integer> in, String errorMessage) {
        requireIn(object, in, null, errorMessage);
    }

    public static void requireIn(Integer object, Set<Integer> in, String errorCode, String errorMessage) {
        requireNonNull(object, formatNonNullMessage("object"));
        requireNonEmpty(in, formatNonNullMessage("in"));

        if (!in.contains(object)) {
            throw new FrameworkException(errorCode, errorMessage);
        }
    }

    // message formatters

    public static String formatMessage(String message, Object... values) {
        return String.format(message, values);
    }

    public static String formatNonNullMessage(Object... values) {
        return formatMessage(DEFAULT_MSG_REQUIRE_NON_NULL, values);
    }

    public static String formatNullMessage(String parameterName) {
        return formatMessage(DEFAULT_MSG_REQUIRE_NULL, parameterName);
    }

    public static String formatNonEmptyMessage(String parameterName) {
        return formatMessage(DEFAULT_MSG_REQUIRE_NON_EMPTY, parameterName);
    }

    public static String formatEmptyMessage(String parameterName) {
        return formatMessage(DEFAULT_MSG_REQUIRE_EMPTY, parameterName);
    }

    public static String formatTrueMessage(String parameterName) {
        return formatMessage(DEFAULT_MSG_REQUIRE_TRUE, parameterName);
    }

    public static String formatFalseMessage(String parameterName) {
        return formatMessage(DEFAULT_MSG_REQUIRE_FALSE, parameterName);
    }

    // private methods


    private static BigDecimal fixPrice(BigDecimal decimal) {
        return decimal.setScale(DEFAULT_PRICE_SCALE, DEFAULT_PRICE_ROUNDING_MODE);
    }

    private static BigDecimal fixPrice(BigDecimal decimal, int scale) {
        return decimal.setScale(scale, DEFAULT_PRICE_ROUNDING_MODE);
    }

}
