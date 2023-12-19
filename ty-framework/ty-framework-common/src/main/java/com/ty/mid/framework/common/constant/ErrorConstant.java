package com.ty.mid.framework.common.constant;

public interface ErrorConstant {

    /**
     * 参数错误
     */
    String CODE_PARAMETER_ERROR = "000000";

    String MSG_PARAMETER_NULL_ERROR = "参数 %s 不能为空";

    String ERROR_PARAMETER_CAN_NOT_BE_NULL = "%s 不能为空";

    String ERROR_CAN_NOT_BE_NULL = "参数不能为空";


    /**
     * 数据不存在
     */
    String CODE_CAN_NOT_OBTAIN_LOCK = "000002";

    String MSG_CAN_NOT_OBTAIN_LOCK = "当前无法获取分布式锁，lock key: %s";

    String CODE_INTERACT_WITH_ESIGN_ERROR = "700010";
}
