package com.ty.mid.framework.api.switcher.exception;

import com.ty.mid.framework.common.exception.FrameworkException;

import java.util.Date;

/**
 * api 关闭异常
 *
 * @author suyouliang
 * @createTime 2023-08-14 15:22
 */
public class ApiDisabledException extends FrameworkException {

    private Date fromDate;

    private Date thruDate;

    private String tipsMessage;

    public ApiDisabledException(Date fromDate, Date thruDate, String tipsMessage) {
        super(tipsMessage);
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.tipsMessage = tipsMessage;
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

    public String getTipsMessage() {
        return tipsMessage;
    }

    public void setTipsMessage(String tipsMessage) {
        this.tipsMessage = tipsMessage;
    }
}
