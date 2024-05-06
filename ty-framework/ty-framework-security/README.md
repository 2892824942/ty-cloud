<!-- TOC -->
* [项目特点](#项目特点)
* [一:框架集成](#一框架集成)
  * [1.引入核心依赖](#1引入核心依赖)
  * [2.配置相关功能](#2配置相关功能)
* [二:功能介绍](#二功能介绍)
  * [1.支持路由鉴权及注解鉴权](#1支持路由鉴权及注解鉴权)
  * [2.全局用户伪装(增强)](#2全局用户伪装增强)
  * [3.用户审计信息实现](#3用户审计信息实现)
  * [4.用户角色菜单鉴权](#4用户角色菜单鉴权)
  * [5.Swagger模块协同配置](#5swagger模块协同配置)
<!-- TOC -->
开源地址:https://github.com/2892824942/ty-cloud/blob/main/ty-framework/ty-framework-security

# 项目特点

- 基于saToken构建的鉴权模块
- 支持路由鉴权及注解鉴权
- 全局用户伪装(增强)
- 用户审计信息实现
- 用户角色菜单鉴权
- Swagger模块协同配置

# 一:框架集成

## 1.引入核心依赖

暂时未发到中央仓库(准备中)...

```xml

<dependency>
    <groupId>com.ty</groupId>
    <artifactId>ty-framework-security-starter</artifactId>
    <version>${最新版本}</version>
</dependency>

```

## 2.配置相关功能

```yaml
#以下功能按需配置
framework:
  security:
    #无需路由鉴权路径
    exclude-pattern: /test/**
    #是否开启注解校验
    enable-annotation: true
    #是否全局开启token伪装能力
    enable-guise: true
    #token伪装能力白名单,默认:所有用户均为白名单 设置后:则仅设置的用户可使用token伪装能力
    enable-guise-user-ids: 10001
```

saToken配置实例
```yaml
sa-token:
  tokenName: token
  is-log: false
  is-print: false
  jwtSecretKey: test123456
  #是否允许同一账号多地同时登录
  is-concurrent: false
  token-style: simple-uuid
  is-read-cookie: false
```

# 二:功能介绍

## 1.支持路由鉴权及注解鉴权
- 通过exclude-pattern配置,可指定无需路由鉴权路径,跳过路由鉴权
- 通过enable-annotation,可开启或关闭注解鉴权.无需写对应的filter配置

## 2.全局用户伪装(增强)
saToken本身的伪装区别: saToken的伪装是请求级别,即开启伪装仅在当前请求内有效(使用lambda则在lambda内有效) 
本框架增强了伪装,将伪装生命周期扩展到全局(基于Interceptor实现),一旦开启,将在失效内一直有效,不受请求限制
使用场景示例:知道用户id的情况下 
- 通过自己的账号直接模拟对应用户,去除造token或找token的成本----非生产环境开发测试使用
- 生产环境紧急救援 部分B端业务,某些特定的bug不好复现,可能存在紧急救援或用户授权登录B端企业账号处理问题的场景
注意:使用时慎重!!!!必须保证入口私密,不要在线上直接暴露(通过授权,密码登方式),否则后果自负

使用:
- 开启全局用户伪装
```yaml
framework:
  security:
    #是否全局开启token伪装能力
    enable-guise: true
    #token伪装能力白名单,默认:所有用户均为白名单 设置后:则仅设置的用户可使用token伪装能力
    enable-guise-user-ids: 10001
```
- 代码调用设置用户A伪装用户即结束伪装
```java
package com.framework.demo.demos.web;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.ty.mid.framework.common.pojo.BaseResult;
import com.ty.mid.framework.common.pojo.Result;
import com.ty.mid.framework.security.utils.LoginHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 系统管理操作测试
 */
@Tag(name = "系统管理操作", description = "给系统开发者提供的便携接口")
@RestController
@RequestMapping("/ops/")
@Valid
@SaCheckRole(value = "admin")
public class OpsController {

  @GetMapping("switchTo")
  @Operation(summary = "当前用户伪装为另一个用户", description = "这个接口一定注意安全,防止恶意利用,确保security配置enableGuise开启,否则无效")
  public BaseResult<String> switchTo(@Schema(description = "用户id") @NotNull(message = "切换的用户id不能为空") String userId) {
    LoginHelper.switchTo(userId);
    return BaseResult.success("切换userId：" + userId + "成功");
  }

  @GetMapping("endSwitch")
  @Operation(summary = "结束伪装为另一个用户", description = "这个接口一定注意安全,防止恶意利用,确保security配置enableGuise开启,否则无效")
  public BaseResult<String> endSwitch() {
    LoginHelper.endSwitch();
    return BaseResult.success("结束切换成功");
  }

  @GetMapping("kickOut")
  @Operation(summary = "会话踢下线")
  public BaseResult<String> kickOut(@Schema(description = "用户id") @NotNull(message = "踢下线用户id不能为空") String userId) {
    StpUtil.kickout(userId);
    return BaseResult.success("用户userId：" + userId + "踢下线成功");
  }
}

```

## 3.用户审计信息实现
当集成service模块时,自动实现审计信息,为service模块提供用户登录上下文信息,完成入库审计字段赋值

## 4.用户角色菜单鉴权
登录时,初始化LoginUser权限相关信息,则可自动完成用户角色菜单鉴权


```java
 package com.framework.demo.demos.web;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.framework.demo.dto.RoleDTO;
import com.framework.demo.dto.UserFullDTO;
import com.framework.demo.enums.ErrorCodeEnum;
import com.framework.demo.service.IUserService;
import com.ty.mid.framework.common.model.LoginUser;
import com.ty.mid.framework.common.pojo.BaseResult;
import com.ty.mid.framework.common.util.AssertUtil;
import com.ty.mid.framework.security.utils.LoginHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.stream.Collectors;

/**
 * 登录测试
 */
@Tag(name = "登录操作", description = "登录相关操作")
@RestController
@RequestMapping("/login/")
@RequiredArgsConstructor
public class LoginController {
  private final IUserService userService;

  @SaIgnore
  @GetMapping("doLogin")
  @Valid
  @Operation(summary = "用户名密码登录")
  public BaseResult<LoginUser> doLogin(@Schema(description = "用户名") @NotBlank(message = "用户名不能为空") String name
          , @Schema(description = "密码") @NotBlank(message = "密码不能为空") String pwd) {
    UserFullDTO userFullDTO = userService.getByUserName(name);
    AssertUtil.notEmpty(userFullDTO, ErrorCodeEnum.LOGIN_FAIL);
    AssertUtil.equals(userFullDTO.getPassword(), pwd, ErrorCodeEnum.LOGIN_FAIL);
    // 校验通过,开始登录
    LoginUser loginUser = new LoginUser();
    loginUser.setUserId(userFullDTO.getId());
    loginUser.setLoginId(userFullDTO.getId());
    loginUser.setUsername(userFullDTO.getName());
    //初始化用户角色权限字段,可直接使用鉴权能力
    loginUser.setRolePermission(userFullDTO.getRoleInfos().stream().map(RoleDTO::getCode).collect(Collectors.toSet()));
    LoginHelper.login(loginUser);
    return BaseResult.success(loginUser);
  }

  @SaIgnore
  @GetMapping("isLogin")
  @Operation(summary = "是否登录")
  public BaseResult<String> isLogin() {
    return BaseResult.success("是否登录：" + StpUtil.isLogin());
  }

  @SaIgnore
  @GetMapping("tokenInfo")
  @Operation(summary = "用户token详情")
  public BaseResult<SaTokenInfo> tokenInfo() {
    return BaseResult.success(StpUtil.getTokenInfo());
  }

  @SaIgnore
  @GetMapping("logout")
  @Operation(summary = "登出")
  public BaseResult<Void> logout() {
    StpUtil.logout();
    return BaseResult.success();
  }

}

```

## 5.Swagger模块协同配置
当saToken配置中的tokenName配置更改后,swagger模块将同步更改,且token位置也会随saToken配置中,readBody,readHead,readCookie配置联动更改

更详细的使用案例,见:https://github.com/2892824942/framework-demo
