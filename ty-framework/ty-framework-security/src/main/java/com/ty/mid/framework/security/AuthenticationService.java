package com.ty.mid.framework.security;

public interface AuthenticationService<T extends AuthToken, R extends AuthenticationResult> {

    /**
     * 登录
     *
     * @param token
     */
    R login(T token);

    /**
     * 退出
     */
    void logout();

}
