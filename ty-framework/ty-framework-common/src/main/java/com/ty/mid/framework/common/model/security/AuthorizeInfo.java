package com.ty.mid.framework.common.model.security;

import com.ty.mid.framework.common.model.Permission;
import com.ty.mid.framework.common.model.Role;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AuthorizeInfo {

    private Collection<Role> roles;
    private Collection<Permission> permissions;

    public AuthorizeInfo(Collection<Role> roles, Collection<Permission> permissions) {
        this.roles = roles == null ? Collections.emptyList() : roles;
        this.permissions = permissions == null ? Collections.emptyList() : permissions;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
