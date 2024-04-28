package com.ty.mid.framework.security.utils;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.ty.mid.framework.common.entity.Auditable;
import com.ty.mid.framework.common.exception.BizException;
import com.ty.mid.framework.common.lang.NeverNull;
import com.ty.mid.framework.common.model.LoginUser;
import com.ty.mid.framework.core.constant.DeviceType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 登录鉴权助手
 * <p>
 * user_type 为 用户类型 同一个用户表 可以有多种用户类型 例如 pc,app
 * deivce 为 设备类型 同一个用户类型 可以有 多种设备类型 例如 web,ios
 * 可以组成 用户类型与设备类型多对多的 权限灵活控制
 * <p>
 * 多用户体系 针对 多种用户类型 但权限控制不一致
 * 可以组成 多用户类型表与多设备类型 分别控制权限
 * 注:为了方法的复用性,LoginHelper返回的LoginUser不会为空,而是使用默认值
 * 主要针对方法可能在登录环境及非登录环境(job,mq场景)调用,不用频繁判空或赋值
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    public static final String LOGIN_USER_KEY = "loginUser";
    public static final String USER_KEY = "userId";
    /**
     * 存储在saToken中的伪装用户信息的key
     */
    public static final String GUISE_KEY = "guise_user";

    /**
     * 登录系统
     *
     * @param loginUser 登录用户信息
     */
    public static void login(LoginUser loginUser) {
        loginByDevice(loginUser, null);
    }

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param loginUser 登录用户信息
     */
    public static void loginByDevice(LoginUser loginUser, DeviceType deviceType) {
        SaStorage storage = SaHolder.getStorage();
        storage.set(LOGIN_USER_KEY, loginUser);
        storage.set(USER_KEY, loginUser.getUserId());
        SaLoginModel model = new SaLoginModel();
        if (ObjectUtil.isNotNull(deviceType)) {
            model.setDevice(deviceType.getDesc());
        }
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
//        UserType userType = UserType.getUserType(loginUser.getUserType());
//        if (userType == UserType.SYS_USER) {
//            model.setTimeout(86400).setActiveTimeout(1800);
//        } else if (userType == UserType.APP_USER) {
//            model.setTimeout(86400).setActiveTimeout(1800);
//        }
        StpUtil.login(loginUser.getLoginId(), model.setExtra(USER_KEY, loginUser.getUserId()));
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 获取用户(多级缓存)
     */
    public static @NeverNull LoginUser getLoginUser() {
        LoginUser loginUser = (LoginUser) SaHolder.getStorage().get(LOGIN_USER_KEY);
        if (loginUser != null) {
            return loginUser;
        }
        SaSession session = StpUtil.getTokenSession();
        if (ObjectUtil.isNull(session)) {
            return LoginUser.DEFAULT_LOGIN_USER;
        }
        loginUser = (LoginUser) session.get(LOGIN_USER_KEY);
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        return loginUser;
    }

    /**
     * 获取用户基于token
     */
    public static @NeverNull LoginUser getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (ObjectUtil.isNull(session)) {
            return LoginUser.DEFAULT_LOGIN_USER;
        }
        return (LoginUser) session.get(LOGIN_USER_KEY);
    }

    /**
     * 获取用户id
     */
    public static long getUserId() {
        Long userId;
        try {
            userId = Convert.toLong(SaHolder.getStorage().get(USER_KEY));
            if (ObjectUtil.isNull(userId)) {
                userId = Convert.toLong(StpUtil.getExtra(USER_KEY));
                SaHolder.getStorage().set(USER_KEY, userId);
            }
        } catch (Exception e) {
            return Auditable.DEFAULT_USER_ID;
        }
        return userId;
    }


    /**
     * 获取用户账户
     */
    public static String getUsername() {
        return getLoginUser().getUsername();
    }


    /**
     * 伪装成另外一个用户
     * 和saToken本身的伪装区别:
     * saToken的伪装是请求级别,即开启伪装仅在当前请求内有效(使用lambda则在lambda内有效)
     * 本类:增强了伪装,将伪装生命周期扩展到全局(基于Filter实现),一旦开启,将在失效内一直有效,不受请求限制
     *
     * 使用场景示例:知道用户id的情况下
     * 1.通过自己的账号直接模拟对应用户,去除造token或找token的成本----非生产环境开发测试使用
     * 2.生产环境紧急救援
     * 部分B端业务,某些特定的bug不好复现,可能存在紧急救援或用户授权登录B端企业账号处理问题的场景
     *
     * 注意:使用时慎重!!!!必须保证入口私密,不要在线上直接暴露(通过授权,密码登方式),否则后果自负
     * @See
     */
    public static String getUserGuise() {
        SaTokenDao saTokenDao = SaManager.getSaTokenDao();
        if (StpUtil.isLogin()){
            Object loginId = StpUtil.getLoginId();
            return saTokenDao.get(getGuiseKey(loginId));
        }
        return null;
    }


    /**
     * 伪装成另外一个用户
     * 和saToken本身的伪装区别:
     * saToken的伪装是请求级别,即开启伪装仅在当前请求内有效(使用lambda则在lambda内有效)
     * 本类:增强了伪装,将伪装生命周期扩展到全局(基于Interceptor实现),一旦开启,将在失效内一直有效,不受请求限制
     * <p>
     * 使用场景示例:知道用户id的情况下
     * 1.通过自己的账号直接模拟对应用户,去除造token或找token的成本----非生产环境开发测试使用
     * 2.生产环境紧急救援
     * 部分B端业务,某些特定的bug不好复现,可能存在紧急救援或用户授权登录B端企业账号处理问题的场景
     *
     * 注意:使用时慎重!!!!必须保证入口私密,不要在线上直接暴露(通过授权,密码登方式),否则后果自负
     * @see  com.ty.mid.framework.security.core.interceptor.UserGuiseInterceptor
     */
    public static void switchTo(String userId) {
        if (Objects.isNull(userId)){
            throw new BizException("伪装的用户id不能为空");
        }
        SaTokenDao saTokenDao = SaManager.getSaTokenDao();
        Object loginId = StpUtil.getLoginId();
        saTokenDao.set(getGuiseKey(loginId),userId,60*60);
    }


    /**
     * 结束伪装成另外一个用户
     * 具体用法
     * @see LoginHelper#switchTo(String)
     */
    public static void endSwitch(String userId) {
        SaTokenDao saTokenDao = SaManager.getSaTokenDao();
        Object loginId = StpUtil.getLoginId();
        saTokenDao.delete(getGuiseKey(loginId));
    }

    private static String getGuiseKey(Object userId){
        return GUISE_KEY.concat("_").concat(userId.toString());
    }

}
