package com.ty.mid.framework.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ty.mid.framework.common.lang.NonNull;
import com.ty.mid.framework.common.lang.Nullable;
import com.ty.mid.framework.common.util.DateUtils;

import java.util.Date;

/**
 * 时效性标记
 */
public interface Timeliness {

    @NonNull
    Date getFromDate();

    @Nullable
    Date getToDate();

    @JsonIgnore
    default boolean isTimeValid() {
        return this.isTimeValid(DateUtils.now());
    }

    @JsonIgnore
    default boolean isTimeValid(@NonNull Date date) {
        return DateUtils.isDateInRange(date, this.getFromDate(), this.getToDate());
    }

}
