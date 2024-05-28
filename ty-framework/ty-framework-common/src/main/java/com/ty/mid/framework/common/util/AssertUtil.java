package com.ty.mid.framework.common.util;

import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.util.ObjectUtil;
import com.ty.mid.framework.common.constant.BaseCode;
import com.ty.mid.framework.common.exception.DataException;
import com.ty.mid.framework.common.exception.enums.GlobalErrorCodeEnum;
import com.ty.mid.framework.common.lang.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 业务断言工具类
 * 返回异常会被包装,最终抛给用户
 * 如内部使用,无需抛给用户使用hu-tool的断言
 *
 * @see cn.hutool.core.lang.Assert
 */
@Slf4j
@ThreadSafe
public class AssertUtil {

    public static final String DEFAULT_ERROR_CODE = GlobalErrorCodeEnum.EXCEPTION.getCode();

    public static String DEFAULT_MSG_REQUIRE_NON_EMPTY = "[%s]不能为空！";
    public static String DEFAULT_MSG_REQUIRE_EMPTY = "[%s]必须为空！";
    public static String DEFAULT_MSG_REQUIRE_TRUE = "[%s]必须为True！";
    public static String DEFAULT_MSG_REQUIRE_FALSE = "[%s]必须为False！";
    public static String DEFAULT_MSG_REQUIRE_IN = "[%s]数据不存在！";

    /**
     * 数据不为空:自动判断null,空字符,空数组,空迭代器,空Map
     *
     * @param data
     * @param errorMessage
     */
    public static void notEmpty(Object data, @NotBlank String errorMessage) {
        notEmpty(data, DEFAULT_ERROR_CODE, errorMessage);
    }

    /**
     * 数据不为空:自动判断null,空字符,空数组,空迭代器,空Map
     */
    public static void notEmpty(Object data, @NotNull BaseCode baseCode) {
        notEmpty(data, baseCode.getCode(), baseCode.getMessage());
    }

    /**
     * 数据不为空:自动判断null,空字符,空数组,空迭代器,空Map
     */
    public static void notEmpty(Object data, @NotBlank String errorCode, @NotBlank String errorMessage) {
        notEmpty(data, errorCode, errorMessage, null);
    }

    /**
     * 数据不为空:自动判断null,空字符,空数组,空迭代器,空Map
     */
    public static void notEmpty(Object data, @NotBlank String errorCode, @NotBlank String errorMessage, VoidFunc0 voidFunc) {
        if (ObjectUtil.isEmpty(data)) {
            doThrowException(errorCode, errorMessage, voidFunc);
        }
    }


    /**
     * 数据为空:自动判断null,空字符,空数组,空迭代器,空Map
     */
    public static void isEmpty(Object data, @NotBlank String errorMessage) {
        isEmpty(data, DEFAULT_ERROR_CODE, errorMessage);
    }

    /**
     * 数据为空:自动判断null,空字符,空数组,空迭代器,空Map
     */
    public static void isEmpty(Object data, @NotNull BaseCode baseCode) {
        isEmpty(data, baseCode.getCode(), baseCode.getMessage());
    }

    /**
     * 数据为空:自动判断null,空字符,空数组,空迭代器,空Map
     */
    public static void isEmpty(Object data, String errorCode, @NotBlank String errorMessage) {
        isEmpty(data, errorCode, errorMessage, null);
    }

    /**
     * 数据为空:自动判断null,空字符,空数组,空迭代器,空Map
     */
    public static void isEmpty(Object data, String errorCode, @NotBlank String errorMessage, VoidFunc0 voidFunc) {
        if (ObjectUtil.isNotEmpty(data)) {
            doThrowException(errorCode, errorMessage, voidFunc);
        }
    }

    /**
     * 表达式为true
     */
    public static void isTrue(final boolean expression, @NotBlank String errorMessage) {
        isTrue(expression, DEFAULT_ERROR_CODE, errorMessage);
    }

    /**
     * 表达式为true
     */
    public static void isTrue(final boolean expression, @NotNull BaseCode baseCode) {
        isTrue(expression, baseCode.getCode(), baseCode.getMessage());
    }

    /**
     * 表达式为true
     */
    public static void isTrue(final boolean expression, @NotBlank String errorCode, @NotBlank String errorMessage) {
        isTrue(expression, errorCode, errorMessage, null);
    }

    /**
     * 表达式为true
     */
    public static void isTrue(final boolean expression, @NotBlank String errorCode, @NotBlank String errorMessage, VoidFunc0 voidFunc) {
        if (!expression) {
            doThrowException(errorCode, errorMessage, voidFunc);
        }
    }

    /**
     * 表达式为false
     */
    public static void isFalse(final boolean expression, @NotBlank String errorMessage) {
        isFalse(expression, DEFAULT_ERROR_CODE, errorMessage);
    }

    /**
     * 表达式为false
     */
    public static void isFalse(final boolean expression, @NotNull BaseCode baseCode) {
        isFalse(expression, baseCode.getCode(), baseCode.getMessage());
    }

    /**
     * 表达式为false
     */
    public static void isFalse(final boolean expression, @NotBlank String errorCode, @NotBlank String errorMessage) {
        isFalse(expression, errorCode, errorMessage, null);
    }

    /**
     * 表达式为false
     */
    public static void isFalse(final boolean expression, @NotBlank String errorCode, @NotBlank String errorMessage, VoidFunc0 voidFunc) {
        if (expression) {
            doThrowException(errorCode, errorMessage, voidFunc);
        }
    }

    /**
     * 对象equals,调用实际类型的equals方法
     */
    public static void equals(Object a, Object b, @NotBlank String errorMessage) {
        equals(a, b, DEFAULT_ERROR_CODE, errorMessage);
    }


    /**
     * 对象equals,调用实际类型的equals方法
     */
    public static void equals(Object a, Object b, @NotNull BaseCode baseCode) {
        equals(a, b, baseCode.getCode(), baseCode.getMessage());
    }

    /**
     * 对象equals,调用实际类型的equals方法
     */
    public static void equals(Object a, Object b, @NotBlank String errorCode, @NotBlank String errorMessage) {
        equals(a, b, errorCode, errorMessage, null);
    }

    /**
     * 对象equals,调用实际类型的equals方法
     */
    public static void equals(Object a, Object b, @NotBlank String errorCode, @NotBlank String errorMessage, VoidFunc0 voidFunc) {
        if (!Objects.equals(a, b)) {
            doThrowException(errorCode, errorMessage, voidFunc);
        }
    }

    /**
     * 数据应在范围内
     */
    public static void in(String data, Iterable<?> dataIterator, String errorMessage) {
        in(data, dataIterator, DEFAULT_ERROR_CODE, errorMessage);
    }

    /**
     * 数据应在范围内
     */
    public static void in(String data, Iterable<?> dataIterator, BaseCode baseCode) {
        in(data, dataIterator, baseCode.getCode(), baseCode.getMessage());
    }

    /**
     * 数据应在范围内
     */
    public static void in(Object data, Iterable<?> dataIterator, String errorCode, String errorMessage) {
        in(data, dataIterator, errorCode, errorMessage, null);
    }

    /**
     * 数据应在范围内
     */
    public static void in(Object data, Iterable<?> dataIterator, String errorCode, String errorMessage, VoidFunc0 voidFunc) {
        if (ObjectUtil.isEmpty(dataIterator)) {
            doThrowException(errorCode, errorMessage, voidFunc);
            return;
        }
        while (dataIterator.iterator().hasNext()) {
            if (Objects.equals(data, dataIterator.iterator().next())) {
                break;
            }
        }
        doThrowException(errorCode, errorMessage, voidFunc);
    }


    /**
     * 抛出异常钱做操作,此操作如异常直接忽略
     *
     * @param voidFunc
     */
    private static void handleVoidFunc(VoidFunc0 voidFunc) {
        if (Objects.nonNull(voidFunc)) {
            try {
                voidFunc.call();
            } catch (Exception e) {
                //ignore
            }
        }
    }

    public static String defNonEmptyMsg(Object... dataues) {
        return defMsg(DEFAULT_MSG_REQUIRE_NON_EMPTY, dataues);
    }

    public static String defEmptyMsg(String parameterName) {
        return defMsg(DEFAULT_MSG_REQUIRE_EMPTY, parameterName);
    }

    public static String defTrueMsg(String parameterName) {
        return defMsg(DEFAULT_MSG_REQUIRE_TRUE, parameterName);
    }

    public static String defFalseMsg(String parameterName) {
        return defMsg(DEFAULT_MSG_REQUIRE_FALSE, parameterName);
    }

    public static String defInMsg(String parameterName) {
        return defMsg(DEFAULT_MSG_REQUIRE_IN, parameterName);
    }

    public static String defMsg(String message, Object... parameterNames) {
        return String.format(message, parameterNames);
    }


    private static void doThrowException(String errorCode, String errorMessage) {
        doThrowException(errorCode, errorMessage, null);
    }

    private static void doThrowException(String errorCode, String errorMessage, VoidFunc0 voidFunc) {
        handleVoidFunc(voidFunc);
        throw DataException.of(errorCode, errorMessage);
    }

}
