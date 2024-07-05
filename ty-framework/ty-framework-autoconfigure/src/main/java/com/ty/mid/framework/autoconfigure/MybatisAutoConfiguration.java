package com.ty.mid.framework.autoconfigure;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.ty.mid.framework.common.audit.AuditorInfoResolver;
import com.ty.mid.framework.common.audit.DefaultLongAuditorInfoResolver;
import com.ty.mid.framework.mybatisplus.core.handler.AuditorMetaObjectHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.Iterator;

/**
 * MyBaits 配置类 <p>
 *
 * @author suyouliang
 */

@MapperScan("com.ty.mid.framework.mybatisplus.core.mapper")
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
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler defaultMetaObjectHandler(ObjectProvider<AuditorInfoResolver<Long>> auditorInfoResolvers) {
        // 自动填充参数类
        Iterator<AuditorInfoResolver<Long>> iterator = auditorInfoResolvers.stream().iterator();
        //如果默认没有定义审计处理器,使用默认的
        if (!iterator.hasNext()) {
            DefaultLongAuditorInfoResolver longDefaultLongAuditorInfoResolver = new DefaultLongAuditorInfoResolver();
            return new AuditorMetaObjectHandler<>(longDefaultLongAuditorInfoResolver);
        }
        //使用定义的
        return new AuditorMetaObjectHandler<>(iterator.next());
    }

    /**
     * 乐观锁插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(MybatisPlusInterceptor mybatisPlusInterceptor) {
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisPlusInterceptor;
    }

}
