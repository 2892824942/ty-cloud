package com.ty.mid.framework.security.shiro.realm;

import com.ty.mid.framework.security.RealmSupport;
import com.ty.mid.framework.security.SecurityService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * this class only for test
 */
public class TestingRealm extends AuthorizingRealm implements RealmSupport {
    @Override
    public SecurityService getSecurityService() {
        return null;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return null;
    }
}
