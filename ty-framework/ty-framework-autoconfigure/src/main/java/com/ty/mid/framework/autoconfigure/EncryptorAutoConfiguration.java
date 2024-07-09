package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.encrypt.config.EncryptorConfig;
import com.ty.mid.framework.encrypt.core.manager.CommonEncryptorManager;
import com.ty.mid.framework.encrypt.core.manager.DesensitizeEncryptorManager;
import com.ty.mid.framework.encrypt.core.manager.EncryptorManager;
import com.ty.mid.framework.encrypt.core.manager.HashIdEncryptorManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 加解密配置
 *
 * @author suyouliang
 * @version 4.6.0
 */
@EnableConfigurationProperties(EncryptorConfig.class)
@ConditionalOnProperty(prefix = EncryptorConfig.PREFIX, name = "enable", havingValue = "true")
public class EncryptorAutoConfiguration {

    @Bean
    @Primary
    public EncryptorManager commonEncryptorManager(EncryptorConfig encryptorConfig) {
        return new CommonEncryptorManager(encryptorConfig);
    }

    @Bean
    public DesensitizeEncryptorManager desensitizeEncryptorManager(EncryptorConfig encryptorConfig) {
        return new DesensitizeEncryptorManager(encryptorConfig);
    }

    @Bean
    public HashIdEncryptorManager hashIdEncryptorManager(EncryptorConfig encryptorConfig) {
        return new HashIdEncryptorManager(encryptorConfig);
    }


}
