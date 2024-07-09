package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.manager.EncryptorManager;
import com.ty.mid.framework.encrypt.mybatis.interceptor.MybatisDecryptInterceptor;
import com.ty.mid.framework.encrypt.mybatis.interceptor.MybatisEncryptInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * MyBaits 注解加解密配置类 <p>
 *
 * @author suyouliang
 */

@RequiredArgsConstructor
@AutoConfigureAfter(EncryptorAutoConfiguration.class)
@ConditionalOnClass(EncryptorManager.class)
@ConditionalOnBean(EncryptorManager.class)
@ConditionalOnProperty(prefix = EncryptorConfig.PREFIX, name = "enable", havingValue = "true")
public class MybatisEncryptionConfiguration {

    /**
     * 加密拦截器
     *
     * @param encryptorManager
     * @return
     */
    @Bean
    public MybatisEncryptInterceptor mybatisEncryptInterceptor(EncryptorManager encryptorManager) {
        return new MybatisEncryptInterceptor(encryptorManager);
    }

    /**
     * 解密拦截器
     *
     * @param encryptorManager
     * @return
     */
    @Bean
    public MybatisDecryptInterceptor mybatisDecryptInterceptor(EncryptorManager encryptorManager) {
        return new MybatisDecryptInterceptor(encryptorManager);
    }

}
