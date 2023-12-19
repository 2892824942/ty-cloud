package com.ty.mid.framework.security;

import java.io.Serializable;

public interface AuthenticationResult extends Serializable {

    String getAccessToken();

}
