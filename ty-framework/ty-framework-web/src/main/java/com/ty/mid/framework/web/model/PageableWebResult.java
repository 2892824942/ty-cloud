package com.ty.mid.framework.web.model;

import lombok.Data;

import java.util.Collection;

@Data
public class PageableWebResult<T> extends WebResult<Collection<T>> {

    private long totalRecords = 0;

    private long totalPages = 0;

    private long size = 10;

    private long current = 1;

    private String sort;

    private boolean hasNext = false;

    private boolean hasPrevious = false;

}
