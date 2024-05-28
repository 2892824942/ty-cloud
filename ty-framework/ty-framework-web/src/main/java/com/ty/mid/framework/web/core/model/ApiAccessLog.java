package com.ty.mid.framework.web.core.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * API 访问日志 <p>
 *
 * @author suyouliang
 */
@Data
public class ApiAccessLog extends ApiLog {
    /**
     * 开始请求时间
     */
    @NotNull(message = "开始请求时间不能为空")
    private LocalDateTime beginTime;
    /**
     * 结束请求时间
     */
    @NotNull(message = "结束请求时间不能为空")
    private LocalDateTime endTime;
    /**
     * 执行时长，单位：毫秒
     */
    @NotNull(message = "执行时长不能为空")
    private Integer duration;

}
