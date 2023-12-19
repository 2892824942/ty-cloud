package com.ty.mid.framework.security.support;

import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.core.cache.Cache;
import com.ty.mid.framework.core.config.SecurityConfiguration;
import com.ty.mid.framework.security.AuthToken;
import com.ty.mid.framework.security.RestTokenManager;
import com.ty.mid.framework.security.RestTokenParser;
import com.ty.mid.framework.security.TokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author suyouliang
 * @createTime 2019-07-07 10:10
 */
@Slf4j
public abstract class AbstractRestTokenManager<T, A extends AuthToken> implements RestTokenManager<T, A> {

    protected Cache<String, String> cache;
    private String cacheKeyPrefix;
    private RestTokenParser<T> tokenParser;
    private TokenGenerator tokenGenerator;
    private SecurityConfiguration config;

    public AbstractRestTokenManager(RestTokenParser<T> tokenParser, TokenGenerator tokenGenerator, SecurityConfiguration config, Cache<String, String> cache) {
        this.tokenParser = tokenParser;
        this.config = config;
        this.tokenGenerator = tokenGenerator;
        this.cache = cache;
        this.cacheKeyPrefix = this.config.getCacheKeyPrefix();
    }

    protected String getCacheKey(String principal) {
        return this.cacheKeyPrefix.concat(principal);
    }

    @Override
    public RestTokenParser<T> getTokenParser() {
        return this.tokenParser;
    }

    @Override
    public String generateToken(A tokenData) {
        return tokenGenerator.generateSureToken(tokenData);
    }

    @Override
    public A decodeSureToken(String token) {
        return tokenGenerator.decodeSureToken(token);
    }

    @Override
    public void remove(String principal) {
        String cacheKey = this.getCacheKey(principal);
        cache.invalidate(cacheKey);
    }

    @Override
    public String get(String principal) {
        String cacheKey = this.getCacheKey(principal);
        return this.cache.get(cacheKey);
    }

    @Override
    public String save(String principal, String token) {
        String cacheKey = this.getCacheKey(principal);
        this.cache.put(cacheKey, token, this.config.getTokenValidityTimeUnit(), this.config.getTokenValidity());
        return token;
    }

    @Override
    public Date getExpire(String principal) {
        String cacheKey = this.getCacheKey(principal);
        return this.cache.getExpireAt(cacheKey);
    }

    @Override
    public boolean renew(A authToken, long renewTime, TimeUnit timeUnit) {
        String cacheKey = this.getCacheKey(authToken.getUserLogin());

        if (!this.cache.existsKey(cacheKey)) {
            return false;
        }

        return this.cache.renewCache(cacheKey, renewTime, timeUnit);
    }

    @Override
    public String generateToken(Map<String, ?> tokenData) {
        if (tokenData == null) {
            throw new FrameworkException("can not create token from null object");
        }

        return this.tokenGenerator.generateToken(tokenData);
    }

    @Override
    public Map<String, ?> decodeToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new FrameworkException("can't decrypt token with null");
        }

        return this.tokenGenerator.decodeToken(token);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Map<String, ?> map = this.decodeToken(token);
            Object id = map.get("id");
            String cacheKey = this.getCacheKey(id.toString());
            return this.cache.existsKey(cacheKey);
        } catch (Exception e) {
            log.warn("valid token error", e);
            return false;
        }
    }

}
