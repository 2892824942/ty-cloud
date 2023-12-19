package com.ty.mid.framework.common.dto;

import com.ty.mid.framework.common.model.Geoable;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DefaultGeoInfo implements Geoable, Serializable {

    private Long provinceId;

    private Long cityId;

    private Long countyId;

    private Long streetId;

    private String addressLineOne;

    private String addressLineTwo;

    private Integer coordinateType;

    private BigDecimal lat;

    private BigDecimal lng;

}
