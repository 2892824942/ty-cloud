package com.ty.mid.framework.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
@Data
public class ApplicationConfiguration {

    private final DataBaseEncrypt dataBaseEncrypt = new DataBaseEncrypt();
    private String frontDomain = "";
    private Long operationPartyId = 1L;
    private Integer batchHandleSize = 100;
    /**
     * 应用编码
     */
    private String applicationCode;
    private String host;
    private String protocol; //  http or https


    @Data
    public static class DataBaseEncrypt {
        private String privateKey = "7df1d1a30e579b2bbe8ca74c805affef";
    }
}
