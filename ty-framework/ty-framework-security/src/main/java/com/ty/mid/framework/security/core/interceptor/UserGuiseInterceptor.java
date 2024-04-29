package com.ty.mid.framework.security.core.interceptor;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.ty.mid.framework.security.config.SecurityConfig;
import com.ty.mid.framework.security.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 用户伪装拦截器 基于sa-token 请求级伪装实现的全局伪装 <p>
 * @author 苏友良 
 */
@RequiredArgsConstructor
@Slf4j
public class UserGuiseInterceptor implements HandlerInterceptor {
    private final SecurityConfig securityConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!StpUtil.isLogin()) {
            return true;
        }
        String[] enableGuiseUserIds = securityConfig.getEnableGuiseUserIds();
        String realUserId = String.valueOf(StpUtil.getLoginId());
        boolean enableUserGuise = true;
        if (ArrayUtil.isNotEmpty(enableGuiseUserIds)) {
            enableUserGuise = Arrays.asList(enableGuiseUserIds).contains(realUserId);
        }
        if (!enableUserGuise) {
            return true;
        }
        String userGuise = LoginHelper.getUserGuise();
        if (StrUtil.isEmpty(userGuise)) {
            return true;
        }
        StpUtil.switchTo(userGuise);
        log.warn("用户:{}伪装为用户:{}成功", realUserId, userGuise);
        return true;
    }
}
