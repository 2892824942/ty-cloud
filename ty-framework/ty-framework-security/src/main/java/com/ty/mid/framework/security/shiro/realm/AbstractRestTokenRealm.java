package com.ty.mid.framework.security.shiro.realm;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.common.model.Permission;
import com.ty.mid.framework.common.model.Role;
import com.ty.mid.framework.common.model.UserLogin;
import com.ty.mid.framework.security.RestTokenManager;
import com.ty.mid.framework.security.SecurityService;
import com.ty.mid.framework.security.exception.UnknownAccountException;
import com.ty.mid.framework.security.shiro.token.ShiroRestToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;

import java.io.Serializable;
import java.util.Map;

public class AbstractRestTokenRealm<U extends UserLogin<ID>, R extends Role, P extends Permission, ID>
        extends AbstractAuthorizingRealm<U, R, P, ID> {

    private String idPropertyName = "id";
    private RestTokenManager tokenManager;
    private SecurityService<U, R, P> securityService;

    public AbstractRestTokenRealm(RestTokenManager tokenManager, SecurityService<U, R, P> securityService) {
        super(securityService);
        this.tokenManager = tokenManager;
        this.securityService = securityService;
    }

    public AbstractRestTokenRealm(String idPropertyName, RestTokenManager tokenManager, SecurityService<U, R, P> securityService) {
        super(securityService);
        this.idPropertyName = idPropertyName;
        this.tokenManager = tokenManager;
        this.securityService = securityService;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        ShiroRestToken restToken = (ShiroRestToken) authToken;

        U userLogin = checkRestToken(restToken);

        return new SimpleAuthenticationInfo(userLogin, restToken.getToken(), getName());
    }

    protected U checkRestToken(ShiroRestToken restToken) {
        String token = restToken.getToken();

        if (StrUtil.isEmpty(token)) throw new UnknownAccountException();

        boolean valid = tokenManager.validateToken(token);
        if (!valid) throw new UnknownAccountException();

        Map map = this.tokenManager.decodeToken(token);
        if (!map.containsKey(idPropertyName)) {
            throw new UnknownAccountException();
        }

        Serializable id = Long.valueOf(map.get(idPropertyName).toString());

        // compare token
        String validToken = this.tokenManager.get(String.valueOf(id));
        if (!validToken.equals(token)) {
            throw new UnknownAccountException();
        }

        U userLogin = getSecurityService().getUserLoginById(id);

        checkUserLogin(restToken, userLogin);
        return userLogin;
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        // do nothing here
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && ShiroRestToken.class.isAssignableFrom(token.getClass());
    }

    @Override
    public SecurityService<U, R, P> getSecurityService() {
        return securityService;
    }
}
