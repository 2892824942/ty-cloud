package com.ty.mid.framework.web.core.util;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

import javax.servlet.Filter;

public class WebUtil {
    public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

}
