package com.ty.mid.framework.security.core.service;

import cn.dev33.satoken.stp.StpInterface;
import com.ty.mid.framework.common.model.LoginUser;
import com.ty.mid.framework.security.utils.LoginHelper;

import java.util.*;

/**
 * sa-token 权限管理实现类  <p>
 * 最简单的默认实现,应该根据需要自己实现
 * @author Lion Li 
 */
public class SaPermissionImpl implements StpInterface {

    /**
     * 获取菜单权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        return new ArrayList<>(loginUser.getMenuPermission());
    }

    /**
     * 获取角色权限列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        return new ArrayList<>(loginUser.getRolePermission());
    }
}
