package com.ty.mid.framework.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ty.mid.framework.common.constant.BaseCode;
import com.ty.mid.framework.common.exception.BaseException;
import com.ty.mid.framework.common.exception.enums.GlobalErrorCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@ToString(exclude = "extraData")
@Data
public class BaseResult<T> implements Serializable, Result<T> {
    @Schema(description = "响应code码 0为成功 其他为失败")
    private String code = "0";
    @Schema(description = "响应信息")
    private String message = "";
    @Schema(description = "响应数据实体")
    private T data;
    /**
     * 分页场景表示总条数
     */
    @Schema(description = "分页场景表示总条数,其他场景无意义")
    private Long totalCount;
    @JsonIgnore
    private Map<String, Object> extraData = new HashMap<>();

    /**
     * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓静态方法↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */
    public static <T> BaseResult<T> success() {
        return new BaseResult<>();
    }
    public static <T> BaseResult<T> success(T data) {
        BaseResult<T> result = new BaseResult<>();
        result.setData(data);

        return result;
    }

    public static <T> BaseResult<List<T>> successPage(PageResult<T> pageResult) {
        BaseResult<List<T>> result = new BaseResult<>();
        result.setData(pageResult.getList());
        result.setTotalCount(pageResult.getTotal());
        return result;
    }

    public static <T> BaseResult<T> fail(String message) {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(GlobalErrorCodeEnum.FAIL.getCode());
        result.setMessage(message);
        return result;
    }

    public static <T> BaseResult<T> fail(String errorCode, String message) {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(errorCode);
        result.setMessage(message);

        return result;
    }

    public static <T> BaseResult<T> fail(int errorCode, String message) {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(String.valueOf(errorCode));
        result.setMessage(message);

        return result;
    }

    /**
     * 通用异常构建返回值
     *
     * @param e
     * @param <T>
     * @return
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
     *
     * @param baseCodeEnum
     * @param <T>
     * @return
     */

    public static <T> BaseResult<T> fail(BaseCode baseCodeEnum) {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(GlobalErrorCodeEnum.FAIL.getCode());
        result.setCode(baseCodeEnum.getCode());
        result.setMessage(baseCodeEnum.getMessage());
        return result;
    }

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
}
