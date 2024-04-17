package com.ty.mid.framework.security.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import com.ty.mid.framework.common.audit.AuditorInfoResolver;
import com.ty.mid.framework.common.entity.Auditable;
import com.ty.mid.framework.security.audit.SaTokenAuditorInfoResolver;
import com.ty.mid.framework.security.core.dao.CacheSaTokenDao;
import com.ty.mid.framework.security.core.service.SaPermissionImpl;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.io.Serializable;

/**
 * Sa-Token 配置
 *
 * @author Lion Li
 */
@AutoConfiguration
public class SaTokenConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    /**
     * 权限接口实现(使用bean注入方便用户替换)
     */
    @Bean
    @ConditionalOnMissingBean
    public StpInterface stpInterface() {
        return new SaPermissionImpl();
    }

    /**
     * 自定义dao层存储
     * 当系统中存在RedissonClient bean时,且系统没有定义SaTokenDao,则使用框架默认的,基于redisson实现的saToken存储
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedissonClient.class)
    public SaTokenDao saTokenDao() {
        return new CacheSaTokenDao();
    }

    /**
     * 默认注入Long为Id的处理类
     * 如果项目使用默认id不为Long,重新注入此方法
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditorInfoResolver<Long> auditorInfoResolver() {
        return new SaTokenAuditorInfoResolver();
    }
}
