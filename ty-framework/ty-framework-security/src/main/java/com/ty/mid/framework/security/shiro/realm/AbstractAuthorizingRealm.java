package com.ty.mid.framework.security.shiro.realm;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.common.model.Permission;
import com.ty.mid.framework.common.model.Role;
import com.ty.mid.framework.common.model.UserLogin;
import com.ty.mid.framework.common.model.security.AuthorizeInfo;
import com.ty.mid.framework.security.RealmSupport;
import com.ty.mid.framework.security.SecurityService;
import com.ty.mid.framework.security.shiro.permission.UrlPatternPermission;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AbstractAuthorizingRealm<U extends UserLogin<ID>, R extends Role, P extends Permission, ID>
        extends AuthorizingRealm implements RealmSupport<U, R, P, ID> {

    protected SecurityService<U, R, P> securityService;

    public AbstractAuthorizingRealm(SecurityService<U, R, P> securityService) {
        this.securityService = securityService;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserLogin userLogin = principals.oneByType(UserLogin.class);

        AuthorizeInfo authorize = authorize((U) userLogin);

        Collection<Role> roles = authorize.getRoles();
        Collection<Permission> permissions = authorize.getPermissions();

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roles.stream().map(r -> r.getRoleCode()).collect(Collectors.toSet()));

        permissions.stream().forEach(p -> {
            if (!StrUtil.isEmpty(p.getPermissionCode()) && p.getPermissionCode().startsWith("/")) {
                info.addObjectPermission(new UrlPatternPermission(p.getPermissionCode()));
            } else if (!StrUtil.isEmpty(p.getPermissionCode()) && p.getPermissionCode().contains(":")) {
                info.addObjectPermission(new WildcardPermission(p.getPermissionCode()));
            } else {
                info.addStringPermission(p.getPermissionCode());
            }
        });

        return info;
    }

    @Override
    public SecurityService<U, R, P> getSecurityService() {
        return securityService;
    }
}
