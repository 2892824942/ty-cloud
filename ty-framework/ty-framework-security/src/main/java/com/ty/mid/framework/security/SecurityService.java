package com.ty.mid.framework.security;


import com.ty.mid.framework.common.model.Permission;
import com.ty.mid.framework.common.model.Role;
import com.ty.mid.framework.common.model.UserLogin;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public interface SecurityService<U extends UserLogin, R extends Role, P extends Permission> {

    U getUserLoginById(Serializable id);

    U findUserLoginByAccount(String account);

    U findUserLoginByEmail(String email);

    U findUserLoginByMobile(String phone);

    Collection<R> getUserRoles(U userLogin);

    Collection<P> getUserPermissions(U userLogin);

    Collection<P> getRolePermissions(R role);

    Set<String> getUserRoleCodes(U userLogin);

    Set<String> getUserPermissionCodes(U userLogin);

}
