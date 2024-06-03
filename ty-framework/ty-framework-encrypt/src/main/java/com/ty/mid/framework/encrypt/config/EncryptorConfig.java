package com.ty.mid.framework.encrypt.config;

import com.ty.mid.framework.core.config.AbstractConfig;
import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
     * 加解密总开关
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
     * 加密盐,HashId
     */
    private HashId hashId=new HashId();

    /**
     * 编码方式，base64/hex
     */
    private EncodeType encode;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HashId {

        /**
         * 是否开启，默认为 true
         */
        private boolean enable = false;
        /**
         * 自定义盐
         */
        private String salt = "helloWorld123";

        /**
         * 最小长度
         */
        private int minLength = 8;

    }

}
