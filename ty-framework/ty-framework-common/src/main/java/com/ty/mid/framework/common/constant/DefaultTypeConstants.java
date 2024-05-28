package com.ty.mid.framework.common.constant;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 类型数据默认值常量类 <p>
 *
 * @author suyoulinag
 */
public interface DefaultTypeConstants {

    /**
     * LocalDateTime时间类型
     */
    LocalDateTime DEFAULT_LOCAL_DATE_TIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    /**
     * LocalDate时间类型
     */
    LocalDate DEFAULT_LOCAL_DATE = LocalDate.of(1970, 1, 1);
    /**
     * DateTime时间类型
     */
    DateTime DEFAULT_DATE_TIME = new DateTime("1970-01-01 00:00:00");
    /**
     * Date时间类型
     */
    Date DEFAULT_DATE = DateUtil.parse("1970-01-01 00:00:00");
    /**
     * Integer类型
     */
    Integer DEFAULT_INTEGER = -1;
    /**
     * Long类型
     */
    Long DEFAULT_LONG = -1L;

}
