package com.ty.mid.framework.common.pojo;

import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ty.mid.framework.common.exception.BizException;
import com.ty.mid.framework.common.exception.FrameworkException;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public interface Result<T> extends Serializable {


    String getMessage();

    T getData();

    @JsonIgnore
    void setExtraData(String key, Object value);

    @JsonIgnore
    Map<String, Object> getExtraData();

    /**
     * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓成员方法↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */
    /**
     * @return
     */
    @JsonIgnore
    boolean isSuccess();

    /**
     * 数据为空:自动判断null,空字符,空数组,空迭代器,空Map
     *
     * @return
     */
    default boolean haveData() {
        if (!isSuccess()) {
            return false;
        }
        return ObjectUtil.isEmpty(getData());
    }

    /**
     * 如果访问失败或数据为空则会抛出异常
     * 数据为空:自动判断null,空字符,空数组,空迭代器,空Map
     *
     * @return
     */
    @JsonIgnore
    default T getDataOrThrow() {
        if (haveData()) {
            return this.getData();
        }
        if (!this.isSuccess()) {
            throw BizException.of(Optional.ofNullable(this.getMessage()).orElse("请求失败"));
        }
        throw BizException.of("数据为空");
    }

    /**
     * 如果访问失败或数据为空则会抛出异常
     * 数据为空:自动判断null,空字符,空数组,空迭代器,空Map
     *
     * @param errorMsg 如果result存在message则使用,否则使用传递的errorMsg
     * @return
     */
    @JsonIgnore
    default T getDataOrThrow(String errorMsg) {
        if (haveData()) {
            return this.getData();
        }
        if (!this.isSuccess()) {
            throw BizException.of(Optional.ofNullable(this.getMessage()).orElse(errorMsg));
        }
        throw BizException.of(errorMsg);
    }

    /**
     * 如果访问失败或数据为空则会抛出异常
     * 数据为空:自动判断null,空字符,空数组,空迭代器,空Map
     * 允许在抛异常前做操作,此操作中的异常将被忽略
     * 主要用于：抛异常前打log日志或其他事情
     *
     * @param voidFunc0 自定义操作
     * @return
     */
    @JsonIgnore
    default T getDataOrThrow(VoidFunc0 voidFunc0) {
        if (haveData()) {
            return this.getData();
        }
        try {
            if (Objects.nonNull(voidFunc0)) {
                voidFunc0.call();
            }
        } catch (Exception e) {
            //ignore e
        }
        if (!this.isSuccess()) {
            throw BizException.of(Optional.ofNullable(this.getMessage()).orElse("请求失败"));
        }


        throw BizException.of("数据不存在");
    }

    /**
     * 如果访问失败或数据为空则会抛出异常
     * 数据为空:自动判断null,空字符,空数组,空迭代器,空Map
     * 允许在抛异常前做操作,此操作中的异常将被忽略
     * 主要用于：抛异常前打log日志或其他事情
     *
     * @param voidFunc0 自定义操作
     * @param errorMsg  如果result存在message则使用,否则使用传递的errorMsg
     * @return
     */
    @JsonIgnore
    default T getDataOrThrow(VoidFunc0 voidFunc0, String errorMsg) {
        if (haveData()) {
            return this.getData();
        }
        if (!this.isSuccess()) {
            throw BizException.of(Optional.ofNullable(this.getMessage()).orElse(errorMsg));
        }
        try {
            if (Objects.nonNull(voidFunc0)) {
                voidFunc0.call();
            }
        } catch (Exception e) {
            //ignore e
        }

        throw BizException.of(errorMsg);
    }


    static <T> BaseResult<T> success(T data) {
        BaseResult<T> result = new BaseResult<>();
        result.setData(data);

        return result;
    }




}
