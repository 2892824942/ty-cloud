package com.ty.mid.framework.security.token;

import com.ty.mid.framework.security.AuthToken;
import lombok.Data;

@Data
public class OAuthToken implements AuthToken {

    private String code;

    @Override
    public String getUserLogin() {
        return code;
    }
}
