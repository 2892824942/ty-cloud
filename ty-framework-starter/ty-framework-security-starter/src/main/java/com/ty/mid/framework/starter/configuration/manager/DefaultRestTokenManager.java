package com.ty.mid.framework.starter.configuration.manager;

import com.ty.mid.framework.core.cache.Cache;
import com.ty.mid.framework.core.config.SecurityConfiguration;
import com.ty.mid.framework.security.RestTokenParser;
import com.ty.mid.framework.security.TokenGenerator;
import com.ty.mid.framework.security.shiro.ShiroToken;
import com.ty.mid.framework.security.support.AbstractRestTokenManager;

import javax.servlet.http.HttpServletRequest;

/**
 * 如果项目使用的鉴权类型和默认类型不匹配，可参考本类自由扩展
 */

public class DefaultRestTokenManager extends AbstractRestTokenManager<HttpServletRequest, ShiroToken> {

    public DefaultRestTokenManager(RestTokenParser<HttpServletRequest> tokenParser, TokenGenerator tokenGenerator, SecurityConfiguration config, Cache<String, String> cache) {
        super(tokenParser, tokenGenerator, config, cache);
    }
}
