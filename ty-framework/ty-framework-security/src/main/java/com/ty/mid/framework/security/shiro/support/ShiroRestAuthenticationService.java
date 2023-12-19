package com.ty.mid.framework.security.shiro.support;

import com.ty.mid.framework.common.constant.DomainConstant;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.model.UserLogin;
import com.ty.mid.framework.common.util.collection.MiscUtils;
import com.ty.mid.framework.core.bus.EventPublisher;
import com.ty.mid.framework.core.util.RandomUtils;
import com.ty.mid.framework.security.AuthenticationResult;
import com.ty.mid.framework.security.AuthenticationService;
import com.ty.mid.framework.security.RestTokenManager;
import com.ty.mid.framework.security.auth.DefaultAuthenticationResult;
import com.ty.mid.framework.security.event.*;
import com.ty.mid.framework.security.shiro.ShiroToken;
import com.ty.mid.framework.security.token.VerifyCodeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@Slf4j
public class ShiroRestAuthenticationService<T extends ShiroToken, R extends AuthenticationResult> implements AuthenticationService<T, R> {

    protected boolean randomToken = true;
    private EventPublisher publisher;
    private RestTokenManager<?, T> tokenManager;

    public ShiroRestAuthenticationService(EventPublisher publisher, RestTokenManager<?, T> tokenManager, boolean randomToken) {
        this.publisher = publisher;
        this.tokenManager = tokenManager;
        this.randomToken = randomToken;
    }

    protected String getIdPropertyName() {
        return DomainConstant.Fields.ID;
    }

    protected String getAuthLoginEventTopic() {
        return "auth.login";
    }

    @Override
    public R login(T token) {

        // login
        this.publisher.publish(new PreLoginEvent<>(getAuthLoginEventTopic(), token));
        try {
            SecurityUtils.getSubject().login(token);
            this.publisher.publish(new PostLoginEvent<>(getAuthLoginEventTopic(), token));
        } catch (Exception e) {
            this.publisher.publish(new LoginFailEvent<>(getAuthLoginEventTopic(), token));
            String errorMsg;
            if (token instanceof VerifyCodeToken) {
                errorMsg = "验证码错误";
            } else {
                errorMsg = "账号或密码错误";
            }
            throw new FrameworkException(errorMsg);
        }

        Map<String, String> tokenData = this.createTokenData(token);
        String accessToken = this.tokenManager.generateToken(tokenData);
        this.tokenManager.save(tokenData.get(getIdPropertyName()), accessToken);

        AuthenticationResult result = new DefaultAuthenticationResult(accessToken);

        UserLogin<? extends Serializable> userLogin = com.ty.mid.framework.security.shiro.util.SecurityUtils.currentUserLogin(UserLogin.class);
        this.publisher.publish(new LoginSuccessEvent<>(result, userLogin));
        //TODO AuthenticationResult 的抽象有问题
        return (R) result;
    }

    protected Map<String, String> createTokenData(T token) {
        UserLogin user = (UserLogin) SecurityUtils.getSubject().getPrincipal();
        Map<String, String> ret = MiscUtils.toMap(getIdPropertyName(), String.valueOf(user.getId()));
        if (randomToken) {
            ret.put("_random", String.valueOf(RandomUtils.randomInt(0, 99999)));
        }
        return ret;
    }

    @Override
    public void logout() {
        if (!com.ty.mid.framework.security.shiro.util.SecurityUtils.isAuthenticated()) {
            return;
        }

        try {
            UserLogin<? extends Serializable> user = com.ty.mid.framework.security.shiro.util.SecurityUtils.currentUserLogin(UserLogin.class);
            SecurityUtils.getSubject().logout();
            this.publisher.publish(new LogoutEvent<>(user));
            tokenManager.remove(String.valueOf(user.getId()));
        } catch (Exception e) {
            log.warn("logout fail", e);
        }
    }
}
