package com.ty.mid.framework.security.shiro.util;

import com.ty.mid.framework.common.model.UserLogin;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;

import java.io.Serializable;
import java.util.Collection;

public abstract class SecurityUtils {


    protected SecurityUtils() {
    }

    /**
     * 判断当前用户是否登录
     *
     * @return 认证过(authenticated) true<br/>
     * 记住我    true <br/>
     * 其他： false
     */
    public static boolean hasLogin() {

        return isAuthenticated() || isRemembered();
    }

    /**
     * 用过户是否认证过
     *
     * @return
     */
    public static boolean isAuthenticated() {
        return getSubject().isAuthenticated();
    }

    /**
     * 用户是否通过记住我登录
     *
     * @return
     */
    public static boolean isRemembered() {
        return getSubject().isRemembered();
    }

    /**
     * 获取当前登录用户
     *
     * @param clazz
     * @param <T>
     * @param <ID>
     * @return
     */
    public static <T extends UserLogin<ID>, ID extends Serializable> T currentUserLogin(Class<T> clazz) {
        if (!hasLogin())
            throw new UnauthenticatedException();

        return getSubject().getPrincipals().oneByType(clazz);
    }

    public static boolean isPermitted(String permission) {
        return getSubject().isPermitted(permission);
    }

    public static boolean isPermittedAll(String... permissions) {
        return getSubject().isPermittedAll(permissions);
    }

    public static boolean hasAllRoles(Collection<String> roles) {
        return getSubject().hasAllRoles(roles);
    }

    public static boolean hasRole(String role) {
        return getSubject().hasRole(role);
    }

    private static Subject getSubject() {
        return org.apache.shiro.SecurityUtils.getSubject();
    }


}
