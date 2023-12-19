package com.ty.mid.framework.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "framework.idempotent")
public class IdempotentConfiguration {

    private boolean enable = true;

    private String lockKeyPrefix = "idempotent";
    private long lockTimeout = 3;
    private TimeUnit lockTimeUnit = TimeUnit.SECONDS;

    private long lockTtlInSeconds = 48 * 60 * 60L;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getLockKeyPrefix() {
        return lockKeyPrefix;
    }

    public void setLockKeyPrefix(String lockKeyPrefix) {
        this.lockKeyPrefix = lockKeyPrefix;
    }

    public long getLockTimeout() {
        return lockTimeout;
    }

    public void setLockTimeout(long lockTimeout) {
        this.lockTimeout = lockTimeout;
    }

    public TimeUnit getLockTimeUnit() {
        return lockTimeUnit;
    }

    public void setLockTimeUnit(TimeUnit lockTimeUnit) {
        this.lockTimeUnit = lockTimeUnit;
    }

    public long getLockTtlInSeconds() {
        return lockTtlInSeconds;
    }

    public void setLockTtlInSeconds(long lockTtlInSeconds) {
        this.lockTtlInSeconds = lockTtlInSeconds;
    }
}