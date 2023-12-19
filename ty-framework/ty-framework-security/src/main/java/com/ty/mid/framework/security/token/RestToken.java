package com.ty.mid.framework.security.token;

import com.ty.mid.framework.security.AuthToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RestToken implements AuthToken {

    private String token;

    @Override
    public String getUserLogin() {
        return token;
    }
}
