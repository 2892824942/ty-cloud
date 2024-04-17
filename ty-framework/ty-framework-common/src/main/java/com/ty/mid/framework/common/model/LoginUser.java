package com.ty.mid.framework.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@Data
@NoArgsConstructor
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 菜单权限
     */
    private Set<String> menuPermission = new HashSet<>();

    /**
     * 角色权限
     */
    private Set<String> rolePermission = new HashSet<>();


}
