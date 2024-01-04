package com.ty.mid.framework.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ty.mid.framework.common.lang.NonNull;
import com.ty.mid.framework.common.lang.Nullable;
import com.ty.mid.framework.common.util.DateUtils;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 时效性标记
 */
public interface Timeliness {

    @NonNull
    LocalDateTime getFromDate();

    @Nullable
    LocalDateTime getToDate();

}
