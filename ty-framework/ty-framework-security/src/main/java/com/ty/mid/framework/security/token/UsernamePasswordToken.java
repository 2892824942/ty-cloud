package com.ty.mid.framework.security.token;

import com.ty.mid.framework.security.AuthToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsernamePasswordToken implements AuthToken {

    private String username;

    private String password;

    @Override
    public String getUserLogin() {
        return username;
    }
}
