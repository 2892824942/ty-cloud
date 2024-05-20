package com.ty.mid.framework.encrypt.config;

import com.ty.mid.framework.core.config.AbstractConfig;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.ty.mid.framework.encrypt.config.EncryptorConfig.PREFIX;

/**
 * 加解密属性配置类
 *
 * @author suyouliang
 * @version 4.6.0
 */
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(PREFIX)
@Data
public class EncryptorConfig extends AbstractConfig {
    public static final String PREFIX = FRAMEWORK_PREFIX + "encrypt";

    /**
     * 加解密开关
     */
    private Boolean enable;

    /**
     * 默认算法
     */
    private AlgorithmType algorithm;

    /**
     * 安全秘钥
     */
    private String password;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 编码方式，base64/hex
     */
    private EncodeType encode;

}
