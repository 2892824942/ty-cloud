package com.ty.mid.framework.web.core.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * API 错误日志
 *
 * @author 芋道源码
 */
@Data
public class ApiErrorLog extends ApiLog{

    /**
     * 异常时间
     */
    @NotNull(message = "异常时间不能为空")
    private LocalDateTime exceptionTime;
    /**
     * 异常名
     */
    @NotNull(message = "异常名不能为空")
    private String exceptionName;
    /**
     * 异常发生的类全名
     */
    @NotNull(message = "异常发生的类全名不能为空")
    private String exceptionClassName;
    /**
     * 异常发生的类文件
     */
    @NotNull(message = "异常发生的类文件不能为空")
    private String exceptionFileName;
    /**
     * 异常发生的方法名
     */
    @NotNull(message = "异常发生的方法名不能为空")
    private String exceptionMethodName;
    /**
     * 异常发生的方法所在行
     */
    @NotNull(message = "异常发生的方法所在行不能为空")
    private Integer exceptionLineNumber;
    /**
     * 异常的栈轨迹异常的栈轨迹
     */
    @NotNull(message = "异常的栈轨迹不能为空")
    private String exceptionStackTrace;
    /**
     * 异常导致的根消息
     */
    @NotNull(message = "异常导致的根消息不能为空")
    private String exceptionRootCauseMessage;
    /**
     * 异常导致的消息
     */
    @NotNull(message = "异常导致的消息不能为空")
    private String exceptionMessage;


}
