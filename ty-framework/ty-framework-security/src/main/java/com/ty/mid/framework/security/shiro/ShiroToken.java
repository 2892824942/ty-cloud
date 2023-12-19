package com.ty.mid.framework.security.shiro;

import com.ty.mid.framework.security.AuthToken;
import org.apache.shiro.authc.AuthenticationToken;

public interface ShiroToken extends AuthToken, AuthenticationToken {
}
