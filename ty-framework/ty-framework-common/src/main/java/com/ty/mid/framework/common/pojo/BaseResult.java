package com.ty.mid.framework.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ty.mid.framework.common.constant.BaseCodeEnum;
import com.ty.mid.framework.common.constant.CommonCodeEnum;
import com.ty.mid.framework.common.exception.BaseException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@ToString(exclude = "extraData")
@Data
public class BaseResult<T> implements Serializable, Result<T> {

    private String code = "0";

    private String message = "";

    private T data;
    /**
     * 分页场景表示总条数
     */
    private Long totalCount;
    @JsonIgnore
    private Map<String, Object> extraData = new HashMap<>();

    @Override
    public boolean isSuccess() {
        return "0".equals(code);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public void setExtraData(String key, Object value) {
        extraData.put(key, value);
    }

    @Override
    public Map<String, Object> getExtraData() {
        return extraData;
    }

    /**
     * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓静态方法↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */
    public static <T> BaseResult<T> success(T data) {
        BaseResult<T> result = new BaseResult<>();
        result.setData(data);

        return result;
    }

    public static <T> BaseResult<? super Collection<T>> successPage(PageResult<T> pageResult) {
        BaseResult<Collection<T>> result = new BaseResult<>();
        result.setData(pageResult.getList());
        result.setTotalCount(pageResult.getTotal());
        return result;
    }

    public static <T> BaseResult<T> fail(String message) {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(CommonCodeEnum.FAIL.getCode());
        result.setMessage(message);
        return result;
    }
    public static <T> BaseResult<T> fail(String errorCode, String message) {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(errorCode);
        result.setMessage(message);

        return result;
    }

    /**
     * 通用异常构建返回值
     * @param e
     * @return
     * @param <T>
     */
    public static <T> BaseResult<T> fail(Exception e) {
        if (e instanceof BaseException) {
            BaseException baseException = (BaseException) e;
            return fail(baseException.getCode(), baseException.getMessage());
        }
        return fail(e.getMessage());
    }

    /**
     * 通用枚举构建返回值
     * @param baseCodeEnum
     * @return
     * @param <T>
     */

    public static <T> BaseResult<T> fail(BaseCodeEnum baseCodeEnum) {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(CommonCodeEnum.FAIL.getCode());
        result.setCode(baseCodeEnum.getCode());
        result.setMessage(baseCodeEnum.getMessage());
        return result;
    }

}
