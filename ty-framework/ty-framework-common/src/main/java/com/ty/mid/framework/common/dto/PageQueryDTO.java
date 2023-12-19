package com.ty.mid.framework.common.dto;

public interface PageQueryDTO {

    int DEFAULT_PAGE_SIZE = 10;

    int getPage();

    int getPageSize();

    String getSort();

}
