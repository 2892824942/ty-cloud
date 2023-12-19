package com.ty.mid.framework.security.shiro.realm;

import com.ty.mid.framework.common.model.Permission;
import com.ty.mid.framework.common.model.Role;
import com.ty.mid.framework.common.model.UserLogin;
import com.ty.mid.framework.core.util.StringUtils;
import com.ty.mid.framework.security.SecurityService;
import com.ty.mid.framework.security.token.UsernamePasswordToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.util.ByteSource;

public abstract class AbstractUsernamePasswordRealm<U extends UserLogin<ID>, R extends Role, P extends Permission, ID>
        extends AbstractAuthorizingRealm<U, R, P, ID> {

    public AbstractUsernamePasswordRealm(SecurityService<U, R, P> securityService) {
        super(securityService);
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authToken;

        U userLogin = authenticate(token);

//        super.checkUserLogin(token, userLogin);

        ByteSource byteSource = this.getByteSource(userLogin.getPasswordSalt());

        return new SimpleAuthenticationInfo(userLogin, token.getPassword(), byteSource, getName());
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && UsernamePasswordToken.class.isAssignableFrom(token.getClass());
    }

    protected ByteSource getByteSource(String salt) {
        if (StringUtils.isEmpty(salt)) {
            return null;
        }
        return ByteSource.Util.bytes(salt);
    }
}
