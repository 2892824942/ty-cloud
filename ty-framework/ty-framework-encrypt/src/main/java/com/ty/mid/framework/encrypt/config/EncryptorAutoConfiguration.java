package com.ty.mid.framework.encrypt.config;

import com.ty.mid.framework.encrypt.core.EncryptorManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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
    public EncryptorManager encryptorManager() {
        return new EncryptorManager();
    }


}
