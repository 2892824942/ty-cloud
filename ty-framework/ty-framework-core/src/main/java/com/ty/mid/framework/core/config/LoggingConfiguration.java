package com.ty.mid.framework.core.config;

import com.ty.mid.framework.common.constant.FrameworkConstant;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("framework.logging")
@Getter
public class LoggingConfiguration {

    private final Logstash logstash = new Logstash();
    private boolean useJsonFormat = FrameworkConstant.Logging.useJsonFormat;

    public boolean isUseJsonFormat() {
        return useJsonFormat;
    }

    public void setUseJsonFormat(boolean useJsonFormat) {
        this.useJsonFormat = useJsonFormat;
    }

    public Logstash getLogstash() {
        return logstash;
    }

    public static class Logstash {

        private boolean enabled = FrameworkConstant.Logging.Logstash.enabled;

        private boolean enableMetrics = false;

        private String host = FrameworkConstant.Logging.Logstash.host;

        private int port = FrameworkConstant.Logging.Logstash.port;

        private int queueSize = FrameworkConstant.Logging.Logstash.queueSize;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnableMetrics() {
            return enableMetrics;
        }

        public void setEnableMetrics(boolean enableMetrics) {
            this.enableMetrics = enableMetrics;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
        }
    }

}
