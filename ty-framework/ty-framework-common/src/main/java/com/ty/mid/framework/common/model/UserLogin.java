package com.ty.mid.framework.common.model;

import java.io.Serializable;

public interface UserLogin<ID> extends Serializable {

    ID getId();

    ID getPartyId();

    String getUserLogin();

    String getEmail();

    String getMobile();

    String getPassword();

    String getPasswordSalt();

    boolean isAccountDisabled();

    boolean isAccountLocked();

}
