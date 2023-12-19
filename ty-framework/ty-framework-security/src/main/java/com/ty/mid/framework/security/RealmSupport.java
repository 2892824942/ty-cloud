package com.ty.mid.framework.security;

import com.ty.mid.framework.common.model.Permission;
import com.ty.mid.framework.common.model.Role;
import com.ty.mid.framework.common.model.UserLogin;
import com.ty.mid.framework.common.model.security.AuthorizeInfo;
import com.ty.mid.framework.security.exception.SecurityException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.LockedAccountException;

import java.util.Collection;

public interface RealmSupport<U extends UserLogin<ID>, R extends Role, P extends Permission, ID> {

    SecurityService<U, R, P> getSecurityService();

    default U getUserLogin(AuthToken token) {
        return getSecurityService().findUserLoginByAccount(token.getUserLogin());
    }

    default U authenticate(AuthToken token) throws SecurityException {
        U userLogin = this.getUserLogin(token);

        // 检查账户禁用、锁定信息
        this.checkUserLogin(token, userLogin);
        // 检查密码
        this.checkCredentialsMatch(token, userLogin);

        return userLogin;
    }

    default AuthorizeInfo authorize(U userLogin) {
        if (userLogin == null) {
            throw new org.apache.shiro.authc.UnknownAccountException("账号密码错误");
        }

        Collection<R> roles = getSecurityService().getUserRoles(userLogin);
        Collection<P> permissions = getSecurityService().getUserPermissions(userLogin);

        return new AuthorizeInfo((Collection<Role>) roles, (Collection<Permission>) permissions);
    }

    default void checkUserLogin(AuthToken token, U userLogin) throws SecurityException {
        if (userLogin == null) {
            throw new org.apache.shiro.authc.UnknownAccountException("账号密码错误");
        }

        if (userLogin.isAccountDisabled()) {
            throw new DisabledAccountException("账号被禁用");
        }

        if (userLogin.isAccountLocked()) {
            throw new LockedAccountException("账号被锁定");
        }

    }

    default void checkCredentialsMatch(AuthToken token, U userLogin) {
    }

}
