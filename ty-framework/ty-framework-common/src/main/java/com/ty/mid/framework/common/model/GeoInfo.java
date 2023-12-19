package com.ty.mid.framework.common.model;

/**
 * 地理信息
 */
public interface GeoInfo {

    /**
     * id
     *
     * @return
     */
    Long getId();

    /**
     * 上级id
     *
     * @return
     */
    Long getParentId();

    /**
     * 名称
     *
     * @return
     */
    String getAreaName();

    /**
     * 级别：
     * 1 => 省
     * 2 => 市
     * 3 => 县
     *
     * @return
     */
    Integer getLevel();

    /**
     * 排序
     *
     * @return
     */
    Integer getSequence();

    /**
     * 全拼
     *
     * @return
     */
    String getFullPinyin();

    /**
     * 短拼
     *
     * @return
     */
    String getShortPinyin();

    /**
     * 首字母拼音
     *
     * @return
     */
    String getFirstLetterPinyin();

    /**
     * 全名
     *
     * @return
     */
    String getFullName();
}
