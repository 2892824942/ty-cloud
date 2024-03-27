package com.ty.mid.framework.common.entity;

import com.ty.mid.framework.common.lang.NonNull;
import com.ty.mid.framework.common.lang.Nullable;

import java.time.LocalDateTime;

/**
 * 时效性标记
 */
public interface Timeliness {

    @NonNull
    LocalDateTime getFromDate();

    @Nullable
    LocalDateTime getToDate();

}
