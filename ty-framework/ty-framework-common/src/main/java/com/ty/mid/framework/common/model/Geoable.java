package com.ty.mid.framework.common.model;

import java.math.BigDecimal;

public interface Geoable {

    /**
     * 省id
     *
     * @return
     */
    Long getProvinceId();

    void setProvinceId(Long provinceId);

    /**
     * 市id
     *
     * @return
     */
    Long getCityId();

    void setCityId(Long cityId);

    /**
     * 区县id
     *
     * @return
     */
    Long getCountyId();

    void setCountyId(Long countyId);

    /**
     * 街道id
     *
     * @return
     */
    Long getStreetId();

    void setStreetId(Long streetId);

    /**
     * 地址1
     *
     * @return
     */
    String getAddressLineOne();

    void setAddressLineOne(String addressLineOne);

    /**
     * 地址2
     *
     * @return
     */
    String getAddressLineTwo();

    void setAddressLineTwo(String addressLineTwo);

    /**
     * 坐标类型
     *
     * @return
     */
    Integer getCoordinateType();

    void setCoordinateType(Integer coordinateType);

    /**
     * 纬度
     *
     * @return
     */
    BigDecimal getLat();

    void setLat(BigDecimal lat);

    /**
     * 经度
     *
     * @return
     */
    BigDecimal getLng();

    void setLng(BigDecimal lng);

    default void fillFrom(Geoable other) {
        if (other == null) {
            return;
        }

        setProvinceId(other.getProvinceId());
        setCityId(other.getCityId());
        setCountyId(other.getCountyId());
        setStreetId(other.getStreetId());
        setAddressLineOne(other.getAddressLineOne());
        setAddressLineTwo(other.getAddressLineTwo());
        setCoordinateType(other.getCoordinateType());
        setLat(other.getLat());
        setLng(other.getLng());
    }


}
