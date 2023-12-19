package com.ty.mid.framework.security.auth;

import com.ty.mid.framework.security.AuthenticationResult;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Data
@Getter
@Setter
public class DefaultAuthenticationResult implements AuthenticationResult {

    private String accessToken;

    public DefaultAuthenticationResult(String accessToken) {
        this.accessToken = accessToken;
    }
}
