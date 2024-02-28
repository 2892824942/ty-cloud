package com.ty.mid.framework.mybatisplus.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.ty.mid.framework.mybatisplus.core.audit.DefaultLongAuditorInfoResolver;
import com.ty.mid.framework.mybatisplus.core.handler.AuditorMetaObjectHandler;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * MyBaits 配置类
 *
 * @author suyouliang
 */

public class MybatisAutoConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 分页插件
        return mybatisPlusInterceptor;
    }

    /**
     * 默认注入Long为Id的处理类
     * 如果项目使用默认id不为Long,重新注入此方法
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler defaultMetaObjectHandler() {
        // 自动填充参数类
        DefaultLongAuditorInfoResolver longDefaultLongAuditorInfoResolver = new DefaultLongAuditorInfoResolver();
        return new AuditorMetaObjectHandler<>(longDefaultLongAuditorInfoResolver);
    }

    /**
     * 新版
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(MybatisPlusInterceptor mybatisPlusInterceptor) {
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
