package com.ty.mid.framework.common.model;

import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.common.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户信息 <p>
 * @author ruoyi 
 */
@Data
@NoArgsConstructor
public class LoginUser implements Serializable {

    public static final LoginUser DEFAULT_LOGIN_USER = new LoginUser();
    public static final String DEFAULT_USER_NAME = "系统";
    private static final long serialVersionUID = 1L;

    static {
        DEFAULT_LOGIN_USER.setUserId(Auditable.DEFAULT_AUDITOR.getId());
        DEFAULT_LOGIN_USER.setLoginId(Auditable.DEFAULT_AUDITOR.getId());
        DEFAULT_LOGIN_USER.setUsername(DEFAULT_USER_NAME);
        DEFAULT_LOGIN_USER.setToken(StrUtil.EMPTY);
    }

    /**
     * 用户ID
     */
    private Long userId;


    /**
     * 用户登录id,不同端同一个用户可能不同
     */
    private Long loginId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 菜单权限,如鉴权使用此菜单对象,则菜单将不会随角色权限更改,需重新登录生效
     * 如需角色权限更改后生效,则需重写获取方法
     */
    private Set<String> menuPermission = new HashSet<>();

    /**
     * 角色权限,如鉴权使用此角色对象,则角色将不会随用户角色更改,需重新登录生效
     * 如需用户角色更改后生效,则需重写获取方法
     */
    private Set<String> rolePermission = new HashSet<>();

    /**
     * 部分场景需要知道当前是否为登录态
     * @return 是否为有效登录态
     */
    public boolean isDefaultUser() {
        return userId <= 0;
    }


}
