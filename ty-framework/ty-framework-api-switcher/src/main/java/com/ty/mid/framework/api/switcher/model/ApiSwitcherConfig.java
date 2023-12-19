package com.ty.mid.framework.api.switcher.model;

import com.ty.mid.framework.core.util.StringUtils;

import java.util.Date;

/**
 * @author suyouliang
 * @createTime 2019-08-14 16:15
 */
public class ApiSwitcherConfig {

    /**
     * api 名称
     */
    private String apiName;

    /**
     * api关闭 开始时间
     */
    private Date fromDate;

    /**
     * api 关闭结束时间
     */
    private Date thruDate;

    /**
     * api 关闭提示
     */
    private String tipMessage;

    public ApiSwitcherConfig(String apiName) {
        this.apiName = apiName;
    }

    public ApiSwitcherConfig(String apiName, Date fromDate, Date thruDate, String tipMessage) {
        this.apiName = apiName;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.tipMessage = tipMessage;
    }

    public boolean isDateDisabled(Date date) {
        if (date == null) {
            return false;
        }

        long now = date.getTime();
        long from = this.fromDate.getTime();

        if (thruDate == null) {
            return now >= from;
        } else {
            return now >= from && now <= thruDate.getTime();
        }
    }

    public boolean isConfigInValid() {
        return StringUtils.isEmpty(apiName) || fromDate == null;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getThruDate() {
        return thruDate;
    }

    public void setThruDate(Date thruDate) {
        this.thruDate = thruDate;
    }

    public String getTipMessage() {
        return tipMessage;
    }

    public void setTipMessage(String tipMessage) {
        this.tipMessage = tipMessage;
    }
}
