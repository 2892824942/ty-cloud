package com.ty.mid.framework.common.constant;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 类型数据默认值常量类 <p>
 *
 * @author suyoulinag
 */
@Getter
public enum DefaultTypeEnum {
    /**
     * LocalDateTime时间类型
     */
    LOCAL_DATE_TIME(LocalDateTime.class) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T defaultValue() {
            return (T) DefaultTypeConstants.DEFAULT_LOCAL_DATE_TIME;
        }
    },
    /**
     * LocalDate时间类型
     */
    LOCAL_DATE(LocalDate.class) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T defaultValue() {
            return (T) DefaultTypeConstants.DEFAULT_LOCAL_DATE;
        }
    },
    /**
     * date时间类型
     */
    DATE(Date.class) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T defaultValue() {
            return (T) DefaultTypeConstants.DEFAULT_DATE;
        }
    },
    /**
     * Long类型
     */
    LONG(Long.class) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T defaultValue() {
            return (T) DefaultTypeConstants.DEFAULT_LONG;
        }
    },
    /**
     * Integer类型
     */
    INTEGER(Integer.class) {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T defaultValue() {
            return (T) DefaultTypeConstants.DEFAULT_INTEGER;
        }
    },
    ;
    public final static Map<Class<?>, DefaultTypeEnum> DEFAULT_TYPE_ENUMMAP = new HashMap<>(7);

    static {
        DefaultTypeEnum[] values = DefaultTypeEnum.values();
        for (DefaultTypeEnum value : values) {
            DEFAULT_TYPE_ENUMMAP.put(value.javaClass, value);
        }
    }

    private final Class<?> javaClass;

    DefaultTypeEnum(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    public abstract <T> T defaultValue();
}
