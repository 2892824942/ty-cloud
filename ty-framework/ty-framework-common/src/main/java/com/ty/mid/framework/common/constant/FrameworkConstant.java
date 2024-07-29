package com.ty.mid.framework.common.constant;

/**
 * @author suyouliang <p>
 * @createTime 2023-08-15 10:07
 */
public interface FrameworkConstant {

    interface Async {

        int corePoolSize = 2;
        int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        int queueCapacity = 10000;
    }


    interface HttpMethod {
        String GET = "GET";
        String HEAD = "HEAD";
        String POST = "POST";
        String PUT = "PUT";
        String PATCH = "PATCH";
        String DELETE = "DELETE";
        String OPTIONS = "OPTIONS";
        String TRACE = "TRACE";
    }


}