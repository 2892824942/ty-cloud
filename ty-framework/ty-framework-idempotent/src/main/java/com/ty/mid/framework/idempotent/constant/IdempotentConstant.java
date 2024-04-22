package com.ty.mid.framework.idempotent.constant;

import java.util.concurrent.TimeUnit;

/**
 * @author suyouliang
 * @createTime 2023-08-15 14:40
 */
public interface IdempotentConstant {

    String DEFAULT_LOCK_KEY_PREFIX = "";

    long DEFAULT_LOCK_TIMEOUT = 5;

    TimeUnit DEFAULT_LOCK_TIME_UNIT = TimeUnit.SECONDS;

    long DEFAULT_TTL_SECONDS = 48 * 60 * 60;

    String EXPRESSION_PREFIX = "#{";
    String EXPRESSION_SUFFIX = "}";

}
